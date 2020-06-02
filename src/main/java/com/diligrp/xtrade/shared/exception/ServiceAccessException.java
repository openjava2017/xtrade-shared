package com.diligrp.xtrade.shared.exception;

/**
 * 远程服务访问异常
 */
public class ServiceAccessException extends RuntimeException {
    /**
     * 错误码
     */
    private int code = 1000;

    /**
     * 是否打印异常栈
     */
    private boolean stackTrace = true;

    public ServiceAccessException(String message) {
        super(message);
    }

    public ServiceAccessException(int code, String message) {
        super(message);
        this.code = code;
        this.stackTrace = false;
    }

    public ServiceAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    @Override
    public Throwable fillInStackTrace() {
        return stackTrace ? super.fillInStackTrace() : this;
    }

    public int getCode() {
        return code;
    }
}
