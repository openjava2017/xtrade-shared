package com.diligrp.xtrade.shared.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DefaultMongoDataSource implements MongoDataSource {
    /**
     * 连接字符串: mongodb://[username:password@]host1[:port1][,host2[:port2],...][/[database][?options]]
     */
    private ConnectionString connectionString;

    /**
     * Mongo客户端
     */
    private volatile MongoClient mongoClient;

    /**
     * 同步锁
     */
    private Object lock = new Object();

    @Override
    public MongoDatabase getDatabase() {
        return getDatabase(connectionString.getDatabase());
    }

    @Override
    public MongoDatabase getDatabase(String database) {
        if (mongoClient == null) {
            synchronized (lock) {
                if (mongoClient == null) {
                    mongoClient = MongoClients.create(connectionString);
                }
            }
        }
        return mongoClient.getDatabase(database);
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public ConnectionString getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(ConnectionString connectionString) {
        this.connectionString = connectionString;
    }
}
