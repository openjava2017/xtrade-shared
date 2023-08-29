package com.diligrp.xtrade.shared.exception;

public class QrCodeException extends RuntimeException {
    /**
     * 错误码
     */
    private int code = 1000;

    /**
     * 是否打印异常栈
     */
    private boolean stackTrace = true;

    public QrCodeException(String message) {
        super(message);
    }

    public QrCodeException(int code, String message) {
        super(message);
        this.code = code;
        this.stackTrace = false;
    }

    public QrCodeException(String message, Throwable ex) {
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
