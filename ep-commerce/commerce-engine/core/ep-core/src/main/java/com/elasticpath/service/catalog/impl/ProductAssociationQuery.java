/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalog.impl;

import java.util.List;

/**
* Groups together a query string and its parameters.
*/
public class ProductAssociationQuery {

	private String queryString;
	private List<Object> queryParameters;

	/**
	 * Creates a query.
	 * @param queryString The query string.
	 * @param queryParameters The query params.
	 */
	public ProductAssociationQuery(final String queryString, final List<Object> queryParameters) {
		this.queryString = queryString;
		this.queryParameters = queryParameters;
	}

	/**
	 * Gets the query string.
	 * @return the query string.
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * Sets the query string.
	 * @param queryString the query string.
	 */
	public void setQueryString(final String queryString) {
		this.queryString = queryString;
	}

	/**
	 * Gets the query params.
	 * @return the query params.
	 */
	public List<Object> getQueryParameters() {
		return queryParameters;
	}

	/**
	 * Sets the query params.
	 * @param queryParameters the query params.
	 */
	public void setQueryParameters(final List<Object> queryParameters) {
		this.queryParameters = queryParameters;
	}
}
