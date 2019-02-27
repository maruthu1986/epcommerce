/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges;

import java.util.Set;

import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;

/**
 * Implementors are responsible for building query strings to be passed to the PersistenceEngine. The query is for getting a list of returns and
 * exchanges that adhere to the specified parameters.
 */
public interface ReturnsAndExchangesPreparedStatementBuilder {

	/**
	 * Builds the main query to all order return skus and the associated object graph.
	 * @return a query builder for getting the return info.
	 */
	JpqlQueryBuilder getReturnInfoPerSkuQueryAndParams();

	/**
	 * Builds the query to retrieve the return taxes only.
	 *
	 * @param taxJournalIds a collection of tax journal ids from order returns query
	 * @return a query builder for getting the tax amount given a set of order numbers
	 */
	JpqlQueryBuilder getTaxesPerReturnQueryAndParams(Set<String> taxJournalIds);

}
