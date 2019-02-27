/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersbystatus;

import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;

/**
 * Interface for the Orders Status Report Statement Builder.
 */
public interface OrdersByStatusPreparedStatementBuilder {

	/**
	 * Builds query to retrieve base order information with one row per order.
	 *
	 * @return a query builder object
	 */
	JpqlQueryBuilder getOrderBaseInfoQueryAndParams();

	/**
	 * Builds a query to retrieve order shipping cost and taxes from physical order shipments,
	 * with one row per order.
	 *
	 * @return a query builder object
	 */
	JpqlQueryBuilder getShippingInfoQueryAndParams();

	/**
	 * Builds a query to retrieve customer information with one row per customer attribute.
	 *
	 * @return a query builder object
	 */
	JpqlQueryBuilder getCustomerInfoQueryAndParams();

	/**
	 * Builds a query to retrieve order return information with one row per order return.
	 *
	 * @return a query builder object
	 */
	JpqlQueryBuilder getReturnInfoQueryAndParams();

}
