/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.reporting.ordersummary;

import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;

/**
 * Interface for Order Summary Statement Builder.
 */
public interface OrderSummaryPreparedStatementBuilder {

	/**
	 * Create query for order summary information.
	 *
	 * The query is grouped by order number and later flattened by day.
	 * 
	 * @return a query builder object containing the querystring and parameters
	 */
	JpqlQueryBuilder getOrderSummaryInfoQueryAndParams();
	
	/**
	 * Builds query to get shipping info from physical order shipments.
	 *
	 * The query is grouped by order number and later flattened by day.
	 *
	 * @return a query builder object containing the querystring and parameters
	 */
	JpqlQueryBuilder getShippingSummaryInfoQueryAndParams();

	}
