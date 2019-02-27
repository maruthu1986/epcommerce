/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.common.pricing.service.impl;

import java.util.Arrays;
import java.util.List;

/**
 * Strategy class which describes a Query that can be executed by the BaseAmountService/Dao.
 */
public class BaseAmountFilterQuery {
	private final String queryName;
	private final String objectGuid;
	private final Object[] searchCriteria;
	private final List<String> guids;

	/**
	 * Constructs a BaseAmountFilterQuery instance.
	 *
	 * @param queryName the name of the JPA query
	 * @param objectGuid the object (product/productSku) guid
	 * @param searchCriteria the search criteria for the query
	 * @param guids product sku guids
	 */
	public BaseAmountFilterQuery(final String queryName, final String objectGuid, final Object[] searchCriteria, final List<String> guids) {
		this.queryName = queryName;
		this.objectGuid = objectGuid;
		this.searchCriteria = Arrays.copyOf(searchCriteria, searchCriteria.length);
		this.guids = guids;
	}

	public String getObjectGuid() {
		return objectGuid;
	}

	public String getQueryName() {
		return queryName;
	}

	public Object[] getSearchCriteria() {
		return Arrays.copyOf(searchCriteria, searchCriteria.length);
	}

	public List<String> getGuids() {
		return guids;
	}
}
