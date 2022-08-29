package com.diligrp.xtrade.shared.config;

import com.diligrp.xtrade.shared.mongo.MongoProperties;
import com.mongodb.client.MongoClients;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MongoClients.class)
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnProperty(prefix = "xtrade", name = "mongo.enable", havingValue = "true")
public class MongoAutoConfiguration {
}
