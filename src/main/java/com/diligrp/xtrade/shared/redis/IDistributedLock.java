package com.diligrp.xtrade.shared.redis;

/**
 * 基于redis的分布式锁接口
 *
 * @author: brenthuang
 * @date: 2017/12/28
 */
public interface IDistributedLock {

    /**
     * 获取分布式锁
     *
     * @param lockKey - 分布式锁的唯一标识
     * @param owner - 分布式锁的拥有者标识，锁释放是也需要指定同一个owner
     * @return true-成功 false-失败
     */
    boolean tryLock(String lockKey, String owner);

    /**
     * 获取分布式锁，如果锁被占用线程将睡眠指定时间后重试，直到指定的超时时间后返回
     * 线程睡眠时间通过最大重试次数和最小睡眠时间计算而来
     *
     * @param lockKey - 分布式锁的唯一标识
     * @param owner - 分布式锁的拥有者标识，锁释放是也需要指定同一个owner
     * @param timeout - 获取锁的超时时间
     * @return true-成功 false-失败
     */
    boolean tryLock(String lockKey, String owner, long timeout);

    /**
     * 根据分布式锁的唯一标识和拥有者释放分布式锁
     *
     * @param lockKey - 分布式锁的唯一标识
     * @param owner - 分布式锁的拥有者标识
     */
    void unlock(String lockKey, String owner);
}
