/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.persistence.support.impl;

import java.util.List;

import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;

/**
 * A criteria-based query used for retrieving data.
 */
public class CriteriaQuery {

	private final String query;
	private final List<Object> parameters;

	/**
	 * Constructor that takes a JpqlQueryBuilder and builds the query text and parameter list.
	 *
	 * @param queryBuilder the query builder used to create the query
	 */
	public CriteriaQuery(final JpqlQueryBuilder queryBuilder) {
		query = queryBuilder.buildQuery();
		parameters = queryBuilder.buildParameterList();
	}

	public String getQuery() {
		return query;
	}

	public List<Object> getParameters() {
		return parameters;
	}
}
