package com.diligrp.xtrade.shared.domain;

import java.time.LocalDate;

/**
 * KEY-ID生成器数据库模型
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class PersistentSequenceKey {
    private Long id;
    /**
     * KEY标识
     */
    private String key;
    /**
     * 起始值
     */
    private Long startWith;
    /**
     * 跨度
     */
    private Long incSpan;
    /**
     * 应用范围
     */
    private String scope;
    /**
     * 数据版本
     */
    private Long version;
    /**
     * 有效日期
     */
    private LocalDate expiredDate;
    /**
     * 当前日期-循环ID生成器使用
     */
    private LocalDate today;
    /**
     * 备注
     */
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getStartWith() {
        return startWith;
    }

    public void setStartWith(Long startWith) {
        this.startWith = startWith;
    }

    public Long getIncSpan() {
        return incSpan;
    }

    public void setIncSpan(Long incSpan) {
        this.incSpan = incSpan;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public LocalDate getToday() {
        return today;
    }

    public void setToday(LocalDate today) {
        this.today = today;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
