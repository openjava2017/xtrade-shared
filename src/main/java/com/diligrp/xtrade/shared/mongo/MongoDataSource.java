package com.diligrp.xtrade.shared.mongo;

import com.mongodb.client.MongoDatabase;

import java.io.Closeable;

public interface MongoDataSource extends Closeable {

    MongoDatabase getDatabase();

    MongoDatabase getDatabase(String database);
}
