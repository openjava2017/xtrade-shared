package com.diligrp.xtrade.shared.redis;

import redis.clients.jedis.Jedis;

import java.io.Closeable;

/**
 * Jedis连接池
 *
 * @author: brenthuang
 * @date: 2018/01/19
 */
public interface JedisDataSource extends Closeable {
    /**
     * 获取Jedis连接
     *
     * 注意：当使用连接池的时候，不再需要人工回收Jedis连接
     * 当调用Jedis.close()方法的时候进行自动回收至连接池中
     * 而且屏蔽了选择使用pool.returnResource()和pool.returnBrokenResource()进行资源释放的细节
     *
     * @return Jedis连接
     */
    Jedis getConnection();
}
