package com.diligrp.xtrade.shared.domain;

import java.util.List;

/**
 * 分页数据模型
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class Page<T> {
	/** 当前第几页 */
	private int pageNumber;
	/** 每页多少条数据 */
	private int pageSize;
	/** 是否需要统计查询条件,默认开启 */
	private transient boolean count = true;
	/** 总记录数 */
	private long total;
	/** 分页数据 */
	private List<T> data;
	/** 总页数 */
	protected long totalPage;

	public Page() {
	}

	public Page(long total, List<T> data) {
		this.total = total;
		this.data = data;
	}

	public Page(int pageNumber, int pageSize) {
		this.setPageNumber(pageNumber);
		this.setPageSize(pageSize);
	}

	public Page(int pageNumber, int pageSize, boolean count) {
		this(pageNumber, pageSize);
		this.count = count;
	}

	public Page(int pageNumber, int pageSize, int totalCount, List<T> data) {
		this(pageNumber, pageSize, false);
		this.total = totalCount;
		this.data = data;
	}

	public void setPageNumber(int pageNumber) {
		if (pageNumber <= 0) {
			this.pageNumber = 1;
		} else {
			this.pageNumber = pageNumber;
		}
	}

	public void setPageSize(int pageSize) {
		if (pageSize <= 0) {
			this.pageSize = 1;
		} else {
			this.pageSize = pageSize;
		}
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public boolean isCount() {
		return count;
	}

	public void setCount(boolean count) {
		this.count = count;
	}

	public static <T> Page<T> create(long total, List<T> data) {
		return new Page<T>(total, data);
	}

	public long getTotalPage() {
		return total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
}
