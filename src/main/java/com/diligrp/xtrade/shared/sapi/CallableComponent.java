package com.diligrp.xtrade.shared.sapi;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 可调用服务组件注解
 *
 * 系统启动时将扫描所有添加此注解的Spring bean对象，并将其注册到可调用服务引擎CallableServiceEngine中
 * 系统接收到外部请求后，根据调用参数在服务引擎CallableServiceEngine中获取相应的CallableServiceEndpoint进行请求处理
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Component
public @interface CallableComponent {
    /**
     * 可调用服务组件唯一标识
     */
    String id() default "";

    /**
     * 可调用服务组件允许那些方法暴露成可调用服务点对象CallableServiceEndpoint
     */
    String[] methods() default {};
}
