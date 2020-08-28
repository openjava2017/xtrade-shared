package com.diligrp.xtrade.shared.sequence;

import com.diligrp.xtrade.shared.util.AssertUtils;
import com.diligrp.xtrade.shared.util.DateUtils;
import com.diligrp.xtrade.shared.util.RandomUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 雪花算法KeyManager实现
 */
public class SnowflakeKeyManager {

    private Lock locker = new ReentrantLock();

    private final ConcurrentMap<String, IKeyGenerator> keyGenerators = new ConcurrentHashMap<>();

    public IKeyGenerator getKeyGenerator(SnowflakeKey key) {
        AssertUtils.notNull(key, "Miss key parameter");

        String cachedKey = key.identifier();
        IKeyGenerator keyGenerator = keyGenerators.get(cachedKey);
        // First check, no need synchronize code block
        if (keyGenerator == null) {
            boolean result = false;
            try {
                result = locker.tryLock(15, TimeUnit.SECONDS);
                if (!result) {
                    throw new RuntimeException("Timeout to get SnowflakeKeyGenerator for " + key.toString());
                }

                // Double check for performance purpose
                if ((keyGenerator = keyGenerators.get(cachedKey)) == null) {
                    keyGenerator = new SnowflakeKeyGenerator(key.timeBits(), key.workerBits(), key.seqBits());
                    keyGenerators.put(cachedKey, keyGenerator);
                }
            } catch (InterruptedException iex) {
                throw new RuntimeException("Interrupted to get SnowflakeKeyGenerator for " + key.toString(), iex);
            } finally {
                if (result) {
                    locker.unlock();
                }
            }
        }

        return keyGenerator;
    }

    /**
     * 雪花算法ID生成器实现
     */
    private class SnowflakeKeyGenerator implements IKeyGenerator {

        /**
         * Customer based epoch, unit as second. until 2020-08-08 00:00:00
         */
        private long epochSeconds = LocalDateTime.of(2020, 8, 8, 0, 0, 0)
                .toEpochSecond(ZoneOffset.of("+8"));

        /**
         * Stable fields after spring bean initializing
         */
        private BitsAllocator bitsAllocator;
        private long workerId;

        /**
         * Volatile fields caused by nextId()
         */
        private long sequence = 0L;
        private long lastSecond = -1L;

        public SnowflakeKeyGenerator(int timeBits, int workerBits, int seqBits) {
            this.bitsAllocator = new BitsAllocator(timeBits, workerBits, seqBits);
            this.workerId = bitsAllocator.assignWorkerId();
        }

        @Override
        public synchronized long nextId() {
            long currentSecond = getCurrentSecond();

            // Clock moved backwards, refuse to generate uid
            if (currentSecond < lastSecond) {
                long refusedSeconds = lastSecond - currentSecond;
                throw new RuntimeException(String.format("Clock moved backwards. Refusing for %d seconds", refusedSeconds));
            }

            // At the same second, increase sequence
            if (currentSecond == lastSecond) {
                sequence = (sequence + 1) & bitsAllocator.maxSequence;
                // Exceed the max sequence, we wait the next second to generate uid
                if (sequence == 0) {
                    currentSecond = getNextSecond(lastSecond);
                }

                // At the different second, sequence restart from zero
            } else {
                sequence = 0L;
            }

            lastSecond = currentSecond;

            // Allocate bits for UID
            return bitsAllocator.allocate(currentSecond - epochSeconds, workerId, sequence);
        }

        public String parseId(long uid) {
            long totalBits = BitsAllocator.TOTAL_BITS;
            long signBits = bitsAllocator.signBits;
            long timestampBits = bitsAllocator.timestampBits;
            long workerIdBits = bitsAllocator.workerIdBits;
            long sequenceBits = bitsAllocator.sequenceBits;

            // parse UID
            long sequence = (uid << (totalBits - sequenceBits)) >>> (totalBits - sequenceBits);
            long workerId = (uid << (timestampBits + signBits)) >>> (totalBits - workerIdBits);
            long deltaSeconds = uid >>> (workerIdBits + sequenceBits);

            LocalDateTime when = LocalDateTime.ofEpochSecond(epochSeconds + deltaSeconds, 0, ZoneOffset.of("+8"));
            String thatTime = DateUtils.formatDateTime(when);

            // format as string
            return String.format("{\"UID\":\"%d\",\"timestamp\":\"%s\",\"workerId\":\"%d\",\"sequence\":\"%d\"}",
                    uid, thatTime, workerId, sequence);
        }

        /**
         * Get next millisecond
         */
        private long getNextSecond(long lastTimestamp) {
            long timestamp = getCurrentSecond();
            while (timestamp <= lastTimestamp) {
                timestamp = getCurrentSecond();
            }

            return timestamp;
        }

        /**
         * Get current second
         */
        private long getCurrentSecond() {
            long currentSecond = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            if (currentSecond - epochSeconds > bitsAllocator.maxDeltaSeconds) {
                throw new RuntimeException("Timestamp bits is exhausted. Refusing UID generate. Now: " + currentSecond);
            }

            return currentSecond;
        }
    }

    /**
     * Allocate 64 bits for the UID(long)<br>
     * sign (fixed 1bit) -> deltaSecond -> workerId -> sequence(within the same second)
     */
    private class BitsAllocator {
        /**
         * Total 64 bits
         */
        public static final int TOTAL_BITS = 1 << 6;

        /**
         * Bits for [sign-> second-> workId-> sequence]
         */
        private int signBits = 1;
        private final int timestampBits;
        private final int workerIdBits;
        private final int sequenceBits;

        /**
         * Max value for workId & sequence
         */
        private final long maxDeltaSeconds;
        private final long maxWorkerId;
        private final long maxSequence;

        /**
         * Shift for timestamp & workerId
         */
        private final int timestampShift;
        private final int workerIdShift;

        /**
         * Constructor with timestampBits, workerIdBits, sequenceBits<br>
         * The highest bit used for sign, so <code>63</code> bits for timestampBits, workerIdBits, sequenceBits
         */
        public BitsAllocator(int timestampBits, int workerIdBits, int sequenceBits) {
            // make sure allocated 64 bits
            int allocateTotalBits = signBits + timestampBits + workerIdBits + sequenceBits;
            AssertUtils.isTrue(allocateTotalBits == TOTAL_BITS, "allocate not enough 64 bits");

            // initialize bits
            this.timestampBits = timestampBits;
            this.workerIdBits = workerIdBits;
            this.sequenceBits = sequenceBits;

            // initialize max value
            this.maxDeltaSeconds = ~(-1L << timestampBits);
            this.maxWorkerId = ~(-1L << workerIdBits);
            this.maxSequence = ~(-1L << sequenceBits);

            // initialize shift
            this.timestampShift = workerIdBits + sequenceBits;
            this.workerIdShift = sequenceBits;
        }

        /**
         * Allocate bits for UID according to delta seconds & workerId & sequence<br>
         * <b>Note that: </b>The highest bit will always be 0 for sign
         */
        public long allocate(long deltaSeconds, long workerId, long sequence) {
            return (deltaSeconds << timestampShift) | (workerId << workerIdShift) | sequence;
        }

        /**
         * Assign worker id using hash and random number, avoid worker id conflict
         */
        public long assignWorkerId() {
            int h;
            // First hash and index via maxWorkerId, see HashMap
            String randomWorkerId = RandomUtils.randomUUID();
            int hash = (h = randomWorkerId.hashCode()) ^ h >>> 16;
            long workerId =  (maxWorkerId - 1) & hash;

            // Secondly plus random number
            int duration = (int)(maxWorkerId - workerId);
            if (duration > 10) {
                workerId += RandomUtils.randomInt(1, duration);
            }
            return workerId;
        }
    }

    public interface SnowflakeKey {
        // 默认时间戳位数
        int DEFAULT_TIME_BITS = 28;
        // 默认机器标识位数
        int DEFAULT_WORKER_BITS = 22;
        // 默认序号位数
        int DEFAULT_SEQ_BITS = 13;

        String identifier();

        default int timeBits() {
            return DEFAULT_TIME_BITS;
        }

        default int workerBits() {
            return DEFAULT_WORKER_BITS;
        }

        default int seqBits() {
            return DEFAULT_SEQ_BITS;
        }
    }
}