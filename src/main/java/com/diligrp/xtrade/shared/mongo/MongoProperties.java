package com.diligrp.xtrade.shared.mongo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xtrade.mongodb")
public class MongoProperties {
    /**
     * MongoDB connection string
     */
    private String url;

    /**
     * MongoDB default database
     */
    private String database;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
