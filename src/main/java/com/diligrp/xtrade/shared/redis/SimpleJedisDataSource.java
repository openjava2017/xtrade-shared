package com.diligrp.xtrade.shared.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 简单Jedis连接池实现
 * 简单的JedisPool连接池实现-不支持hash一致性算法或者Master-Slave的redis集群
 * ShardedJedisPool - 基于hash一致性算法的redis集群连接池
 * JedisSentinelPool - 基于redis3 master-slave集群的连接池
 *
 * @author: brenthuang
 * @date: 2018/01/19
 */
public class SimpleJedisDataSource extends JedisPoolConfig implements JedisDataSource {
    /**
     * Redis服务器IP
     */
    private String redisHost;
    /**
     * Redis服务器端口
     */
    private int redisPort;
    /**
     * Redis服务器密码
     */
    private String password;
    /**
     * 连接超时时间 - 毫秒
     */
    private int timeout = 30000;
    /**
     * 数据库编号
     */
    private int database = 0;
    /**
     * JedisPool连接池
     */
    private volatile JedisPool jedisPool;
    /**
     * 同步锁
     */
    private Object lock = new Object();

    /**
     * 获取Jedis连接
     *
     * 注意：当使用连接池的时候，不再需要人工回收Jedis连接
     * 当调用Jedis.close()方法的时候进行自动回收至连接池中
     * 而且屏蔽了选择使用pool.returnResource()和pool.returnBrokenResource()进行资源释放的细节
     *
     * @return Jedis连接
     */
    @Override
    public Jedis getConnection() {
        if (jedisPool == null) {
            synchronized (lock) {
                // Double check
                if (jedisPool == null) {
                    jedisPool = new JedisPool(this, redisHost, redisPort, timeout, password, database);
                }
            }
        }
        return jedisPool.getResource();
    }

    @Override
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setDatabase(int database) {
        this.database = database;
    }
}
