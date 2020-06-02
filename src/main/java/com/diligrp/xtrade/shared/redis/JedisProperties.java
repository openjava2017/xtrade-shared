package com.diligrp.xtrade.shared.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xtrade.jedis")
public class JedisProperties {
    /**
     * Database index.
     */
    private int database = 0;

    /**
     * Redis server host.
     */
    private String host = "localhost";

    /**
     * Login password of the redis server.
     */
    private String password;

    /**
     * Redis server port.
     */
    private int port = 6379;

    /**
     * Connection timeout.
     */
    private int timeout = 30000;

    private Pool pool;

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    /**
     * Pool properties.
     */
    public static class Pool {

        /**
         * Maximum number of "idle" connections in the pool. Use a negative value to
         * indicate an unlimited number of idle connections.
         */
        private int maxIdle = 8;

        /**
         * Target for the minimum number of idle connections to maintain in the pool. This
         * setting only has an effect if both it and time between eviction runs are
         * positive.
         */
        private int minIdle = 0;

        /**
         * Maximum number of connections that can be allocated by the pool at a given
         * time. Use a negative value for no limit.
         */
        private int maxActive = 8;

        /**
         * Maximum amount of time a connection allocation should block before throwing an
         * exception when the pool is exhausted. Use a negative value to block
         * indefinitely.
         */
        private long maxWait = -1;

        /**
         * Time between runs of the idle object evictor thread. When positive, the idle
         * object evictor thread starts, otherwise no idle object eviction is performed.
         */
        private long timeBetweenEvictionRuns = 60000; // one minutes by default

        public int getMaxIdle() {
            return this.maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return this.minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxActive() {
            return this.maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public long getMaxWait() {
            return this.maxWait;
        }

        public void setMaxWait(long maxWait) {
            this.maxWait = maxWait;
        }

        public long getTimeBetweenEvictionRuns() {
            return this.timeBetweenEvictionRuns;
        }

        public void setTimeBetweenEvictionRuns(long timeBetweenEvictionRuns) {
            this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
        }
    }
}
