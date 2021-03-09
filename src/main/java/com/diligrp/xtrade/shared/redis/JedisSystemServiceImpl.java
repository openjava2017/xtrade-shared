package com.diligrp.xtrade.shared.redis;

import com.diligrp.xtrade.shared.exception.RedisSystemException;
import com.diligrp.xtrade.shared.util.AssertUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * Redis缓存服务的具体实现类
 * 注意: 本类目前使用的连接池JedisPool不支持Redis集群，仅支持单台服务器
 *
 * @author: brenthuang
 * @date: 2018/01/02
 */
public class JedisSystemServiceImpl implements IRedisSystemService {

    /**
     * Redis连接池配置
     */
    private JedisDataSource dataSource;

    public JedisSystemServiceImpl() {
    }

    public JedisSystemServiceImpl(JedisDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 递增并获取key对应的最新数值
     *
     * @param key - redis key
     * @return key对应的值
     * @throws RedisSystemException
     */
    @Override
    public Long incAndGet(String key) throws RedisSystemException {
        AssertUtils.notEmpty(key, "key must be not empty");

        try (Jedis connection = dataSource.getConnection()) {
            return connection.incr(key);
        } catch (Exception ex) {
            throw new RedisSystemException("Call redis incAndGet exception", ex);
        }
    }

    /**
     * 递增并获取key对应的最新数值，同时指定key超时时间
     *
     * @param key             - redis key
     * @param expireInSeconds - 超时时间，单位秒
     * @return key对应的值
     * @throws RedisSystemException
     */
    @Override
    public Long incAndGet(String key, int expireInSeconds) throws RedisSystemException {
        AssertUtils.notEmpty(key, "key must be not empty");

        try (Jedis connection = dataSource.getConnection()) {
            Pipeline transaction = connection.pipelined();

            Response<Long> result = transaction.incr(key);
            transaction.expire(key, expireInSeconds);
            transaction.sync();
            return result.get();
        } catch (Exception ex) {
            throw new RedisSystemException("Call redis incAndGet exception", ex);
        }
    }

    /**
     * 设置key的值，并指定超时时间
     *
     * @param key             - redis key
     * @param value           - 设置的值
     * @param expireInSeconds - 超时时间，单位秒
     * @throws RedisSystemException
     */
    @Override
    public void setAndExpire(String key, String value, int expireInSeconds) throws RedisSystemException {
        AssertUtils.notEmpty(key, "key must be not empty");
        AssertUtils.notEmpty(value, "value must be not empty");

        try (Jedis connection = dataSource.getConnection()) {
            // We still can use connection.setex to do the same thing
            Pipeline transaction = connection.pipelined();
            transaction.set(key, value);
            transaction.expire(key, expireInSeconds);
            transaction.sync();
        } catch (Exception ex) {
            throw new RedisSystemException("Call redis setAndExpire exception", ex);
        }
    }

    /**
     * 设置key的值
     *
     * @param key   - redis key
     * @param value - 设置的值
     * @throws RedisSystemException
     */
    @Override
    public void set(String key, String value) throws RedisSystemException {
        AssertUtils.notEmpty(key, "key must be not empty");
        AssertUtils.notEmpty(value, "value must be not empty");

        try (Jedis connection = dataSource.getConnection()) {
            connection.set(key, value);
        } catch (Exception ex) {
            throw new RedisSystemException("Call redis set exception", ex);
        }
    }

    /**
     * 获取key的值，并重新设置超时时间
     *
     * @param key             - redis key
     * @param expireInSeconds - 超时时间，单位秒
     * @return key对应的值
     * @throws RedisSystemException
     */
    @Override
    public String getAndExpire(String key, int expireInSeconds) throws RedisSystemException {
        AssertUtils.notEmpty(key, "key must be not empty");

        try (Jedis connection = dataSource.getConnection()) {
            Pipeline transaction = connection.pipelined();

            Response<String> result = transaction.get(key);
            transaction.expire(key, expireInSeconds);
            transaction.sync();
            return result.get();
        } catch (Exception ex) {
            throw new RedisSystemException("Call redis getAndExpire exception", ex);
        }
    }

    /**
     * 获取指定key的值
     *
     * @param key - redis key
     * @return key对应的值
     * @throws RedisSystemException
     */
    @Override
    public String get(String key) throws RedisSystemException {
        AssertUtils.notEmpty(key, "key must be not empty");

        try (Jedis connection = dataSource.getConnection()) {
            return connection.get(key);
        } catch (Exception ex) {
            throw new RedisSystemException("Call redis get exception", ex);
        }
    }

    /**
     * 移除指定keys的值
     *
     * @param keys - 带删除的redis key列表
     * @throws RedisSystemException
     */
    @Override
    public void remove(String... keys) throws RedisSystemException {
        AssertUtils.notEmpty(keys, "keys must be not empty");

        try (Jedis connection = dataSource.getConnection()) {
            connection.del(keys);
        } catch (Exception ex) {
            throw new RedisSystemException("Call redis remove exception", ex);
        }
    }

    /**
     * 自定义执行Redis命令
     */
    @Override
    public void execute(IConnectionCallback callback) throws RedisSystemException {
        try (Jedis connection = dataSource.getConnection()) {
            callback.doInConnection(connection);
        } catch (Exception ex) {
            throw new RedisSystemException("Call redis remove exception", ex);
        }
    }

    public void setDataSource(JedisDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
