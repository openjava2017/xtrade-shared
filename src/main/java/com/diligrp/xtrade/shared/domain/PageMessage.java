package com.diligrp.xtrade.shared.domain;

import java.util.List;

/**
 * 分页数据模型
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class PageMessage<T> extends Message<List<T>>{
	// 总记录数
	private long total;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public static <E> PageMessage<E> success(long total, List<E> data) {
		var page = new PageMessage<E>();
		page.setCode(CODE_SUCCESS);
		page.setTotal(total);
		page.setData(data);
		page.setMessage(MSG_SUCCESS);
		return page;
	}

	public static PageMessage<?> failure(String message) {
		var page = new PageMessage<>();
		page.setCode(CODE_FAILURE);
		page.setTotal(0);
		page.setData(null);
		page.setMessage(message);
		return page;
	}

	public static PageMessage<?> failure(int code, String message) {
		var page = new PageMessage<>();
		page.setCode(code);
		page.setTotal(0);
		page.setData(null);
		page.setMessage(message);
		return page;
	}
}
