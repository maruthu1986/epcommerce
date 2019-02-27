/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.service.impl;

import com.elasticpath.domain.advancedsearch.AdvancedQueryType;

/**
 * Represents an unbuild EPQL query result. @see QueryBuilder
 */
public class UnbuildQueryResult {

	private final AdvancedQueryType queryType;
	
	private final String queryPart;
	
	/**
	 * Constructor.
	 * 
	 * @param queryType query type
	 * @param queryPart query part
	 */
	public UnbuildQueryResult(final AdvancedQueryType queryType, final String queryPart) {
		this.queryType = queryType;
		this.queryPart = queryPart;
	}
	
	/**
	 * Gets query type.
	 * 
	 * @return query type
	 */
	public AdvancedQueryType getQueryType() {
		return queryType;
	}
	
	/**
	 * Gets query part.
	 * 
	 * @return query part
	 */
	public String getQueryPart() {
		return queryPart;
	}
}
