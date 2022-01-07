package com.diligrp.xtrade.shared.exception;

/**
 * 远程服务连接异常
 */
public class ServiceConnectException extends ServiceAccessException {

    public ServiceConnectException(String message) {
        super(message);
    }

    public ServiceConnectException(int code, String message) {
        super(code, message);
    }

    public ServiceConnectException(String message, Throwable ex) {
        super(message, ex);
    }
}
