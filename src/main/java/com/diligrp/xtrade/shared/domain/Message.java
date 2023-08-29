package com.diligrp.xtrade.shared.domain;

/**
 * 接口返回消息数据模型
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class Message<T> {
    protected static final int CODE_SUCCESS = 200;

    protected static final int CODE_FAILURE = 1000;

    protected static final String MSG_SUCCESS = "success";

    // 编码
    private Integer code;
    // 描述
    private String message;
    // 数据
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <E> Message<E> success() {
        var result = new Message<E>();
        result.code = CODE_SUCCESS;
        result.message = MSG_SUCCESS;
        return result;
    }

    public static <E> Message<E> success(E data) {
        var result = new Message<E>();
        result.code = CODE_SUCCESS;
        result.data = data;
        result.message = MSG_SUCCESS;
        return result;
    }

    public static <E> Message<E> failure(String message) {
        var result = new Message<E>();
        result.code = CODE_FAILURE;
        result.message = message;
        return result;
    }

    public static <E> Message<E> failure(int code, String message) {
        var result = new Message<E>();
        result.code = code;
        result.message = message;
        return result;
    }
}
