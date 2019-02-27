/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.commons.pagination.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.Paginator;

/**
 * An abstract implementation of a page involving a pagination adapter.
 *
 * @param <T> the class to be used for this page
 */
public class PageImpl<T> implements Page<T>, Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private List<T> items = new ArrayList<>();

	private final int pageSize;

	private final DirectedSortingField[] orderingFields;

	private int pageNumber;

	private final Paginator<T> paginator;

	/**
	 * @param items the items for this page
	 * @param paginator the
	 * @param page the page
	 */
	public PageImpl(final List<T> items, final Paginator<T> paginator, final Page<T> page) {
		this.items = items;
		this.paginator = paginator;
		this.pageNumber = page.getPageNumber();
		this.pageSize = page.getPageSize();
		this.orderingFields = page.getOrderingFields();
	}

	/**
	 * @param paginator the paginator
	 * @param pageNumber the page number
	 * @param pageSize the page size
	 * @param orderingFields the ordering fields
	 */
	public PageImpl(final Paginator<T> paginator, final int pageNumber, final int pageSize, final DirectedSortingField[] orderingFields) {
		this.paginator = paginator;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.orderingFields = (DirectedSortingField[]) ArrayUtils.clone(orderingFields);
	}

	/**
	 * @return the items belonging to this page
	 */
	@Override
	public List<T> getItems() {
		return items;
	}

	/**
	 * @return the page ending index
	 */
	@Override
	public int getPageEndIndex() {
		if (getPageStartIndex() > 0) {
			return getPageStartIndex() - 1 + Math.min(getPageSize(), getItems().size());
		}
		return 0;
	}

	/**
	 * @return the page size of this page
	 */
	@Override
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the page number of this page
	 */
	@Override
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * @return the starting index of this page
	 */
	@Override
	public int getPageStartIndex() {
		if (getPageNumber() == 1 && paginator.getTotalItems() == 0) {
			return 0;
		}
		if (getPageNumber() > 0) {
			return (getPageNumber() - 1) * getPageSize() + 1;
		}
		return 0;
	}

	/**
	 * @return the total number of items
	 */
	@Override
	public long getTotalItems() {
		return paginator.getTotalItems();
	}

	/**
	 * @return the total number of pages available
	 */
	@Override
	public int getTotalPages() {
		return paginator.getTotalPages();
	}

	/**
	 * @return the field the data is ordered by
	 */
	@Override
	public DirectedSortingField[] getOrderingFields() {
		return (DirectedSortingField[]) ArrayUtils.clone(orderingFields);
	}

	/**
	 * @return the first of the available pages
	 */
	public Page<T> first() {
		return new PageImpl<>(paginator, 1, getPageSize(), getOrderingFields());
	}

	/**
	 * @return the last of the available pages
	 */
	public Page<T> last() {
		return new PageImpl<>(paginator, getTotalPages(), getPageSize(), getOrderingFields());
	}

	/**
	 * @return the next page
	 */
	public PageImpl<T> next() {
		return new PageImpl<>(paginator, getPageNumber() + 1, getPageSize(), getOrderingFields());
	}

	/**
	 * @return the previous page
	 */
	public PageImpl<T> previous() {
		int pageNumber = getPageNumber() - 1;
		if (pageNumber < 1) {
			pageNumber = 1;
		}
		return new PageImpl<>(paginator, pageNumber, getPageSize(), getOrderingFields());
	}

	/**
	 * @param lastPageNumber the last available page number
	 */
	public void limitToLastPage(final int lastPageNumber) {
		pageNumber = Math.min(pageNumber, lastPageNumber);
	}
}
