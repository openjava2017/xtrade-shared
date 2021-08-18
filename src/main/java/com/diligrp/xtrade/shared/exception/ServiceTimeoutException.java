package com.diligrp.xtrade.shared.exception;

/**
 * 远程服务访问超时异常
 */
public class ServiceTimeoutException extends ServiceAccessException {

    public ServiceTimeoutException(String message) {
        super(message);
    }

    public ServiceTimeoutException(int code, String message) {
        super(code, message);
    }

    public ServiceTimeoutException(String message, Throwable ex) {
        super(message, ex);
    }
}
