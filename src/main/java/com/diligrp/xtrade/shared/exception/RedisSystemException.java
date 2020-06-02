package com.diligrp.xtrade.shared.exception;

/**
 * Redis系统异常类
 *
 * @author: brenthuang
 * @date: 2020/12/29
 */
public class RedisSystemException extends Exception {
    public RedisSystemException(String msg) {
        super(msg);
    }

    public RedisSystemException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
