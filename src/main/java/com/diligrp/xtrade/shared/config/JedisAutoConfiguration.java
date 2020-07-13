package com.diligrp.xtrade.shared.config;

import com.diligrp.xtrade.shared.redis.IDistributedLock;
import com.diligrp.xtrade.shared.redis.IRedisSystemService;
import com.diligrp.xtrade.shared.redis.JedisDataSource;
import com.diligrp.xtrade.shared.redis.RedisProperties;
import com.diligrp.xtrade.shared.redis.JedisDistributedLock;
import com.diligrp.xtrade.shared.redis.JedisSystemServiceImpl;
import com.diligrp.xtrade.shared.redis.SimpleJedisDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Jedis.class)
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(prefix = "xtrade", name = "jedis.enable", havingValue = "true")
public class JedisAutoConfiguration {
    @Bean(name = "jedisDataSource")
    @ConditionalOnMissingBean
    public JedisDataSource jedisDataSource(RedisProperties properties) {
        SimpleJedisDataSource dataSource = new SimpleJedisDataSource();
        dataSource.setRedisHost(properties.getHost());
        dataSource.setRedisPort(properties.getPort());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDatabase(properties.getDatabase());
        dataSource.setTimeout(properties.getTimeout());
        RedisProperties.Pool pool = properties.getPool();
        if (pool != null) {
            dataSource.setMinIdle(pool.getMinIdle());
            dataSource.setMaxIdle(pool.getMaxIdle());
            dataSource.setMaxTotal(pool.getMaxActive());
            dataSource.setMaxWaitMillis(pool.getMaxWait());
            dataSource.setTimeBetweenEvictionRunsMillis(pool.getTimeBetweenEvictionRuns());
        }
        return dataSource;
    }

    @Bean(name = "distributedLock")
    @ConditionalOnMissingBean
    public IDistributedLock distributedLock(JedisDataSource dataSource) {
        return new JedisDistributedLock(dataSource);
    }

    @Bean(name = "redisSystemService")
    @ConditionalOnMissingBean
    public IRedisSystemService redisSystemService(JedisDataSource dataSource) {
        return new JedisSystemServiceImpl(dataSource);
    }
}
