package com.diligrp.xtrade.shared.config;

import com.diligrp.xtrade.shared.redis.IDistributedLock;
import com.diligrp.xtrade.shared.redis.IRedisSystemService;
import com.diligrp.xtrade.shared.redis.JedisDataSource;
import com.diligrp.xtrade.shared.redis.JedisProperties;
import com.diligrp.xtrade.shared.redis.RedisDistributedLock;
import com.diligrp.xtrade.shared.redis.RedisSystemServiceImpl;
import com.diligrp.xtrade.shared.redis.SimpleJedisDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Jedis.class)
@EnableConfigurationProperties(JedisProperties.class)
@ConditionalOnProperty(prefix = "xtrade", name = "jedis.enable", havingValue = "true")
public class JedisAutoConfiguration {
    @Bean(name = "jedisDataSource")
    @ConditionalOnMissingBean
    public JedisDataSource jedisDataSource(RedisProperties properties) {
        SimpleJedisDataSource dataSource = new SimpleJedisDataSource();
        dataSource.setDatabase(properties.getDatabase());
        dataSource.setRedisHost(properties.getHost());
        dataSource.setRedisPort(properties.getPort());
        dataSource.setPassword(properties.getPassword());
        Duration timeout = properties.getTimeout();
        dataSource.setTimeout((int) (timeout == null ? 20000 : timeout.toMillis()));
        return dataSource;
    }

    @Bean(name = "distributedLock")
    @ConditionalOnMissingBean
    public IDistributedLock distributedLock(JedisDataSource dataSource) {
        return new RedisDistributedLock(dataSource);
    }

    @Bean(name = "redisSystemService")
    @ConditionalOnMissingBean
    public IRedisSystemService redisSystemService(JedisDataSource dataSource) {
        return new RedisSystemServiceImpl(dataSource);
    }
}
