package com.diligrp.xtrade.shared.config;

import com.diligrp.xtrade.shared.sapi.DefaultCallableServiceEngine;
import com.diligrp.xtrade.shared.sapi.DefaultCallableServiceManager;
import com.diligrp.xtrade.shared.sapi.ICallableServiceEngine;
import com.diligrp.xtrade.shared.sapi.ICallableServiceManager;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(BeanPostProcessor.class)
@ConditionalOnProperty(prefix = "xtrade", name = "sapi-support.enable", havingValue = "true")
public class SapiAutoConfiguration {

    @Bean(name = "callableServiceEngine")
    @ConditionalOnMissingBean
    public ICallableServiceEngine callableServiceEngine() {
        return new DefaultCallableServiceEngine();
    }

    @Bean(name = "callableServiceManager")
    @ConditionalOnMissingBean
    public ICallableServiceManager callableServiceManager(ICallableServiceEngine callableServiceEngine) {
        return new DefaultCallableServiceManager(callableServiceEngine);
    }
}
