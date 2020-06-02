package com.diligrp.xtrade.shared.sequence;

import com.diligrp.xtrade.shared.domain.PersistentSequenceKey;
import com.diligrp.xtrade.shared.util.AssertUtils;
import com.diligrp.xtrade.shared.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SequenceKey和SerialKey管理器实现
 *
 * SequenceKey分布式实现方案：每个服务器节点第一使用SequenceKey时将向数据库请求一组ID（数据库中SequenceKey的startWith=startWith + incSpan）
 * 将SequenceKey缓存在JVM内存中，在使用完这组ID后重新向数据库申请；由于通过数据库乐观锁实现因此每个节点申请到的ID不会重复。
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class KeyGeneratorManager {
    private IKeySynchronizer keySynchronizer;

    private Lock locker = new ReentrantLock();

    private final ConcurrentMap<KeyEntry, IKeyGenerator> keyGenerators = new ConcurrentHashMap<>();

    private Lock serialLocker = new ReentrantLock();

    private final ConcurrentMap<KeyEntry, ISerialKeyGenerator> serialKeyGenerators = new ConcurrentHashMap<>();

    public KeyGeneratorManager() {
    }

    public KeyGeneratorManager(IKeySynchronizer keySynchronizer) {
        this.keySynchronizer = keySynchronizer;
    }

    public IKeyGenerator getKeyGenerator(Enum key) {
        return getKeyGenerator(key, null);
    }

    public IKeyGenerator getKeyGenerator(Enum key, String scope) {
        AssertUtils.notNull(key, "Miss key parameter");

        KeyEntry cachedKey = new KeyEntry(key.toString(), scope);
        IKeyGenerator keyGenerator = keyGenerators.get(cachedKey);
        // First check, no need synchronize code block
        if (keyGenerator == null) {
            boolean result = false;
            try {
                result = locker.tryLock(15, TimeUnit.SECONDS);
                if (!result) {
                    throw new RuntimeException("Timeout to get KeyGenerator for " + key.toString());
                }

                // Double check for performance purpose
                if ((keyGenerator = keyGenerators.get(cachedKey)) == null) {
                    PersistentSequenceKey persistentKey = keySynchronizer.loadSequenceKey(key.toString(), scope);
                    if (persistentKey == null) {
                        throw new RuntimeException("Unregistered sequence key generator: " + key.toString());
                    }
                    keyGenerator = new KeyGeneratorImpl(persistentKey.getId(), key.toString());
                    keyGenerators.put(cachedKey, keyGenerator);
                }
            } catch (InterruptedException iex) {
                throw new RuntimeException("Interrupted to get KeyGenerator for " + key.toString(), iex);
            } finally {
                if (result) {
                    locker.unlock();
                }
            }
        }

        return keyGenerator;
    }

    public ISerialKeyGenerator getSerialKeyGenerator(Enum key) {
        return getSerialKeyGenerator(key, null);
    }

    public ISerialKeyGenerator getSerialKeyGenerator(Enum key, String scope) {
        AssertUtils.notNull(key, "Miss key parameter");

        KeyEntry cachedKey = new KeyEntry(key.toString(), scope);
        ISerialKeyGenerator keyGenerator = serialKeyGenerators.get(cachedKey);
        // First check, no need synchronize code block
        if (keyGenerator == null) {
            boolean result = false;
            try {
                result = serialLocker.tryLock(15, TimeUnit.SECONDS);
                if (!result) {
                    throw new RuntimeException("Timeout to get KeyGenerator for " + key.toString());
                }

                // Double check for performance purpose
                if ((keyGenerator = serialKeyGenerators.get(cachedKey)) == null) {
                    PersistentSequenceKey persistentKey = keySynchronizer.loadSequenceKey(key.toString(), scope);
                    if (persistentKey == null) {
                        throw new RuntimeException("Unregistered sequence key generator: " + key.toString());
                    }
                    keyGenerator = new SerialKeyGeneratorImpl(persistentKey.getId(), key.toString());
                    serialKeyGenerators.put(cachedKey, keyGenerator);
                }
            } catch (InterruptedException iex) {
                throw new RuntimeException("Interrupted to get KeyGenerator for " + key.toString(), iex);
            } finally {
                if (result) {
                    serialLocker.unlock();
                }
            }
        }

        return keyGenerator;
    }

    private class KeyGeneratorImpl implements IKeyGenerator {
        private long id;
        private String key;
        private long startWith;
        private long endWith;
        private Lock keyLocker = new ReentrantLock();

        public KeyGeneratorImpl(long id, String key) {
            this.id = id;
            this.key = key;
            this.startWith = 0;
            this.endWith = -1;
        }

        @Override
        public long nextId() {
            boolean result = false;
            try {
                result = keyLocker.tryLock(15L, TimeUnit.SECONDS);
                if (!result) {
                    throw new RuntimeException("Timeout to get KeyGenerator for " + key);
                }

                if (startWith <= endWith) {
                    return startWith++;
                } else {
                    int retry = 1;
                    for (; ; retry++) {
                        PersistentSequenceKey sequenceKey = keySynchronizer.findSequenceKeyById(id);
                        long newStartWith = sequenceKey.getStartWith() + sequenceKey.getIncSpan();
                        if (keySynchronizer.compareAndSet(id, newStartWith, sequenceKey.getVersion())) {
                            startWith = sequenceKey.getStartWith();
                            endWith = newStartWith - 1;
                            break;
                        }
                        if (retry >= 8) {
                            throw new RuntimeException("Exceed max retry to generate key id for " + key);
                        }
                    }
                    // Then recursive call for a next ID
                    return nextId();
                }
            } catch (InterruptedException iex) {
                throw new RuntimeException("Interrupted to get KeyGenerator for " + key, iex);
            } finally {
                if (result) {
                    keyLocker.unlock();
                }
            }
        }
    }

    private class SerialKeyGeneratorImpl implements ISerialKeyGenerator {
        private long id;
        private String key;

        private SerialKeyGeneratorImpl(long id, String key) {
            this.id = id;
            this.key = key;
        }

        @Override
        public String nextSerialNo(IDatedIdStrategy strategy) {
            //悲观锁添加行锁 - 多JVM多线程场景下自动实现线程同步
            PersistentSequenceKey persistentKey = keySynchronizer.synchronizeSequenceKey(id);
            if (persistentKey == null) {
                throw new RuntimeException("Unregistered sequence key generator: " + key);
            }

            return strategy.id(persistentKey.getToday(), persistentKey.getStartWith());
        }
    }

    private class KeyEntry {
        public String key;
        public String scope;

        public KeyEntry(String key, String scope) {
            this.key = key;
            this.scope = scope;
        }

        @Override
        public int hashCode() {
            // Key must be not null
            int hashCode = key.hashCode();
            if (scope != null) {
                hashCode = hashCode + scope.hashCode();
            }
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof KeyEntry) {
                KeyEntry cachedKey = (KeyEntry) obj;
                // Key must be not null
                if (key.equals(cachedKey.key)) {
                    return ObjectUtils.equals(scope, cachedKey.scope);
                }
            }

            return false;
        }
    }
}
