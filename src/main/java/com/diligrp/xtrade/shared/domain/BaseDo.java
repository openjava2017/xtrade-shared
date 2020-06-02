package com.diligrp.xtrade.shared.domain;

import java.time.LocalDateTime;

public class BaseDo {
    // 数据库主键
    protected Long id;
    // 创建时间
    protected LocalDateTime createdTime;
    // 修改时间
    protected LocalDateTime modifiedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
