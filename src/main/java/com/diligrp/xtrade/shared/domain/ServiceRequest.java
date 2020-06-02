package com.diligrp.xtrade.shared.domain;

/**
 * RPC服务请求模型
 *
 * @author: brenthuang
 * @date: 2018/01/05
 */
public class ServiceRequest<E> {
    private RequestContext context = new RequestContext();

    private E data;

    public RequestContext getContext() {
        return context;
    }

    public void setContext(RequestContext context) {
        this.context = context;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
