package com.diligrp.xtrade.shared.domain;

import com.diligrp.xtrade.shared.util.DateUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * 请求上下文模型
 *
 * @author: brenthuang
 * @date: 2018/01/05
 */
public class RequestContext extends HashMap<String, Object> {
    public String getString(String param) {
        return (String)get(param);
    }

    public Long getLong(String param) {
        Object value = get(param);
        if (value != null) {
            return value instanceof Long ? (Long)value : Long.parseLong(value.toString());
        }
        return null;
    }

    public Integer getInteger(String param) {
        Object value = get(param);
        if (value != null) {
            return value instanceof Integer ? (Integer)value : Integer.parseInt(value.toString());
        }
        return null;
    }

    public LocalDateTime getDateTime(String param) {
        Object value = get(param);
        if (value != null) {
            return value instanceof LocalDateTime ? (LocalDateTime)value :
                    DateUtils.parseDateTime(value.toString(), DateUtils.YYYY_MM_DD_HH_MM_SS);
        }
        return null;
    }

    public <T> T getObject(String param, Class<T> type) {
        Object value = get(param);
        return value == null ? null : type.cast(value);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getObject(String param) {
        Object value = get(param);
        return Optional.ofNullable ((T) value);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<List<T>> getObjects(String param) {
        Object value = get(param);
        return Optional.ofNullable ((List<T>) value);
    }
}