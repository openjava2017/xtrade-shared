package com.diligrp.xtrade.shared.sapi;

import com.diligrp.xtrade.shared.domain.ServiceRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 提供远程服务的可调用服务点的自描述模型
 * 一个远程服务点由调用对象target、对象方法method和参数数据类型Class<E>组成
 *
 * @author: brenthuang
 * @date: 2018/01/05
 */
public class CallableServiceEndpoint<E> {
    /**
     * 提供远程服务的调用对象
     */
    private Object target;

    /**
     * 提供远程服务的对象方法
     */
    private Method method;

    /**
     * 远程服务所需参数数据类型，ServiceRequest<T>中的泛型
     */
    private Class<E> requiredType;

    private CallableServiceEndpoint(Object target, Method method, Class<E> requiredType) {
        this.target = target;
        this.method = method;
        this.requiredType = requiredType;
    }

    public Class<E> getRequiredType() {
        return requiredType;
    }

    public Object call(ServiceRequest<E> request) throws Throwable {
        try {
            return method.invoke(target, request);
        } catch (InvocationTargetException tex) {
            throw tex.getCause() == null ? tex : tex.getCause();
        }
    }

    public static <E> CallableServiceEndpoint<E> create(Object target, Method method, Class<E> requiredType) {
        return new CallableServiceEndpoint<>(target, method, requiredType);
    }
}
