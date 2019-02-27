/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.search.impl;


/**
 * Navigation Search data.
 */
public final class NavigationSearchData {

	private final int pageId;
	private final String searchKeyword;
	private final String scope;

	/**
	 * Constructor.
	 *
	 * @param pageId        page id
	 * @param searchKeyword search keyword
	 * @param scope         scope
	 */
	public NavigationSearchData(final int pageId, final String searchKeyword, final String scope) {
		this.pageId = pageId;
		this.searchKeyword = searchKeyword;
		this.scope = scope;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public String getScope() {
		return scope;
	}

	public int getPageId() {
		return pageId;
	}

}

