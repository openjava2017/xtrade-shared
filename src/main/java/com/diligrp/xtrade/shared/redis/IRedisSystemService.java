package com.diligrp.xtrade.shared.redis;

import com.diligrp.xtrade.shared.exception.RedisSystemException;

/**
 * Redis缓存服务基础接口
 *
 * @author: brenthuang
 * @date: 2017/12/29
 */
public interface IRedisSystemService {
    /**
     * 递增并获取key对应的最新数值
     *
     * @param key - redis key
     * @return key对应的值
     * @throws RedisSystemException
     */
    Long incAndGet(String key) throws RedisSystemException;

    /**
     * 递增并获取key对应的最新数值，同时指定key超时时间
     *
     * @param key - redis key
     * @param expireInSeconds - 超时时间，单位秒
     * @return key对应的值
     * @throws RedisSystemException
     */
    Long incAndGet(String key, int expireInSeconds) throws RedisSystemException;

    /**
     * 设置key的值，并指定超时时间
     *
     * @param key - redis key
     * @param value - 设置的值
     * @param expireInSeconds - 超时时间，单位秒
     * @throws RedisSystemException
     */
    void setAndExpire(String key, String value, int expireInSeconds) throws RedisSystemException;

    /**
     * 设置key的值
     *
     * @param key - redis key
     * @param value - 设置的值
     * @throws RedisSystemException
     */
    void set(String key, String value) throws RedisSystemException;

    /**
     * 获取key的值，并重新设置超时时间
     *
     * @param key - redis key
     * @param expireInSeconds - 超时时间，单位秒
     * @return key对应的值
     * @throws RedisSystemException
     */
    String getAndExpire(String key, int expireInSeconds) throws RedisSystemException;

    /**
     * 获取指定key的值
     *
     * @param key - redis key
     * @return key对应的值
     * @throws RedisSystemException
     */
    String get(String key) throws RedisSystemException;

    /**
     * 移除指定keys的值
     *
     * @param keys - 带删除的redis key列表
     * @throws RedisSystemException
     */
    void remove(String... keys) throws RedisSystemException;
}
