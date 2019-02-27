/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;


/**
 * Keyword Search data.
 */
public final class KeywordSearchData {

	private final int pageId;
	private final int pageSize;
	private final String searchKeyword;
	private final String scope;

	/**
	 * Constructor.
	 *
	 * @param pageId        page id
	 * @param pageSize      page size
	 * @param searchKeyword search keyword
	 * @param scope         scope
	 */
	public KeywordSearchData(final int pageId, final int pageSize, final String searchKeyword, final String scope) {
		this.pageId = pageId;
		this.pageSize = pageSize;
		this.searchKeyword = searchKeyword;
		this.scope = scope;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getScope() {
		return scope;
	}

	public int getPageId() {
		return pageId;
	}

}

