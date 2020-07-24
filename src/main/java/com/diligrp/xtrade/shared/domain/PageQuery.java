package com.diligrp.xtrade.shared.domain;

/**
 * 分页查询领域模型
 */
public class PageQuery {
    // 起始行下标
    protected Integer start;
    // 获取的记录行数
    protected Integer limit;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * 通过页号、每页记录数计算起始行下标
     */
    public void from(int pageNo, int pageSize) {
        this.start = (pageNo - 1) * pageSize;
        this.limit = pageSize;
    }
}
