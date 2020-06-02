package com.diligrp.xtrade.shared.sapi;

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 可调用服务引擎接口类
 * 服务引擎主要用于注册和获取可调用服务点CallableServiceEndpoint
 *
 * @author: brenthuang
 * @date: 2018/01/08
 */
public interface ICallableServiceEngine extends BeanPostProcessor {
    /**
     * 注册可调用服务点
     *
     * @param object - 可调用服务组件对象
     * @param componentId - 服务组件唯一标识
     * @param methods - 服务组件上的方法名
     */
    void registerCallableServiceEndpoints(Object object, String componentId, String... methods);

    /**
     * 获取可调用服务点
     *
     * @param service - 服务名,格式：componentId:methodName
     * @return CallableServiceEndpoint
     */
    CallableServiceEndpoint<?> callableServiceEndpoint(String service);
}
