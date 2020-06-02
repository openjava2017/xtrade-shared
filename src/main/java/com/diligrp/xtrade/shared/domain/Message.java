package com.diligrp.xtrade.shared.domain;

/**
 * 接口返回消息数据模型
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class Message<T> {
    private static final int CODE_SUCCESS = 200;

    private static final int CODE_FAILURE = 1000;

    private static final String MSG_SUCCESS = "success";

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

    public static Message<?> success() {
        Message<?> result = new Message<>();
        result.code = CODE_SUCCESS;
        result.message = MSG_SUCCESS;
        return result;
    }

    public static <E> Message<E> success(E data) {
        Message<E> result = new Message<>();
        result.code = CODE_SUCCESS;
        result.data = data;
        result.message = MSG_SUCCESS;
        return result;
    }

    public static Message<?> failure(String message) {
        Message<?> result = new Message<>();
        result.code = CODE_FAILURE;
        result.message = message;
        return result;
    }

    public static Message<?> failure(int code, String message) {
        Message<?> result = new Message<>();
        result.code = code;
        result.message = message;
        return result;
    }
}
