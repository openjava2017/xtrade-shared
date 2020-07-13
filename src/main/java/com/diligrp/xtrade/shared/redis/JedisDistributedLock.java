package com.diligrp.xtrade.shared.redis;

import com.diligrp.xtrade.shared.util.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁Redis简易实现，通过在Redis服务器端执行LUA脚本保证一组命令执行的原子性。
 * 分布式锁的获取和释放请指定相同的owner，owner2将无法释放owner1获得的分布式锁。
 * RedisDistributedLock为不可重入的分布式锁，即使是同一线程也无法重复获取锁。
 * 考虑到通过超时时间来获取分布式锁的实现方式，请谨慎使用。
 * RedisDistributedLock大部分使用场景为柜员做业务时锁定工位时使用。
 *
 * @author: brenthuang
 * @date: 2017/12/28
 */
public class JedisDistributedLock implements IDistributedLock {
    private static Logger LOG = LoggerFactory.getLogger(JedisDistributedLock.class);

    private static String KEY_FRONTDESK_LOCKER_PREFEX = "boss:lock:";

    private static final Long MAX_RETRY_TIMES = 10L;

    private static final Long MIN_SLEEP_TIME = 100L;

    private static final Long KEY_EXPIRE_TIME = TimeUnit.SECONDS.toMillis(30);

    private static final String LOCK_SCRIPT = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
            "return redis.call('pexpire', KEYS[1], ARGV[2]) else return 0 end";

    private static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * Redis连接池配置
     */
    private JedisDataSource dataSource;

    public JedisDistributedLock() {
    }

    public JedisDistributedLock(JedisDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey - 分布式锁的唯一标识
     * @param owner - 分布式锁的拥有者标识，锁释放是也需要指定同一个owner
     * @return true-成功 false-失败
     */
    @Override
    public boolean tryLock(String lockKey, String owner) {
        AssertUtils.notEmpty(lockKey, "lockKey must be not empty");
        AssertUtils.notEmpty(owner, "owner must be not empty");

        try (Jedis connection = dataSource.getConnection()) {
            String key = KEY_FRONTDESK_LOCKER_PREFEX + lockKey;

            Long result = (Long) connection.eval(LOCK_SCRIPT, Collections.singletonList(key),
                    Arrays.asList(owner, String.valueOf(KEY_EXPIRE_TIME)));
            return result == 1;
        } catch (Exception ex) {
            LOG.error("Distributed lock system exception", ex);
            return false;
        }
    }

    /**
     * 获取分布式锁，如果锁被占用线程将睡眠指定时间后重试，直到指定的超时时间后返回
     * 线程睡眠时间通过最大重试次数和最小睡眠时间计算而来
     *
     * @param lockKey - 分布式锁的唯一标识
     * @param owner - 分布式锁的拥有者标识，锁释放是也需要指定同一个owner
     * @param timeoutInMillis - 获取锁的超时时间
     * @return true-成功 false-失败
     */
    @Override
    public boolean tryLock(String lockKey, String owner, long timeoutInMillis) {
        AssertUtils.notEmpty(lockKey, "lockKey must be not empty");
        AssertUtils.notEmpty(owner, "owner must be not empty");

        try (Jedis connection = dataSource.getConnection()) {
            String key = KEY_FRONTDESK_LOCKER_PREFEX + lockKey;
            Long sleepInMillis = Math.max(timeoutInMillis/MAX_RETRY_TIMES, MIN_SLEEP_TIME);

            Long result;
            long start = System.currentTimeMillis();
            do {
                result = (Long) connection.eval(LOCK_SCRIPT, Collections.singletonList(key),
                        Arrays.asList(owner, String.valueOf(KEY_EXPIRE_TIME)));
                if (result == 1 || timeoutInMillis <= 0) {
                    break;
                }

                try {
                    Thread.sleep(sleepInMillis);
                } catch (InterruptedException iex) {
                    break;
                }
            } while (System.currentTimeMillis() - start < timeoutInMillis);

            return result == 1;
        } catch (Exception ex) {
            LOG.error("Distributed lock system exception", ex);
            return false;
        }
    }

    /**
     * 根据分布式锁的唯一标识和拥有者释放分布式锁
     *
     * @param lockKey - 分布式锁的唯一标识
     * @param owner - 分布式锁的拥有者标识
     */
    @Override
    public void unlock(String lockKey, String owner) {
        AssertUtils.notEmpty(lockKey, "lockKey must be not empty");
        AssertUtils.notEmpty(owner, "owner must be not empty");

        try (Jedis connection = dataSource.getConnection()) {
            String key = KEY_FRONTDESK_LOCKER_PREFEX + lockKey;
            connection.eval(UNLOCK_SCRIPT, Collections.singletonList(key), Collections.singletonList(owner));
        } catch (Exception ex) {
            LOG.error("Distributed lock system exception", ex);
        }
    }

    public void setDataSource(JedisDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
