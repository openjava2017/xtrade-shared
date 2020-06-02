package com.diligrp.xtrade.shared.exception;

/**
 * 数据签名验签时发生的数据异常
 *
 * @author: brenthuang
 * @date: 2020/04/03
 */
public class MessageEnvelopException extends RuntimeException {
    /**
     * 错误码
     */
    private int code = 1000;

    /**
     * 是否打印异常栈
     */
    private boolean stackTrace = true;

    public MessageEnvelopException(String message) {
        super(message);
    }

    public MessageEnvelopException(int code, String message) {
        super(message);
        this.code = code;
        this.stackTrace = false;
    }

    public MessageEnvelopException(String message, Throwable ex) {
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
