/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.stockallocation;

import com.elasticpath.cmclient.reporting.common.PreparedStatement;
import com.elasticpath.cmclient.reporting.stockallocation.parameters.StockAllocationParameters;


/**
 * Implementors are responsible for building query strings to be passed to the PersistenceEngine.
 * The query is for getting a list of orders that are awaiting stock allocation. 
 */
public interface StockAllocationPreparedStatementBuilder {

	/**
	 * Builds the prepared statement, creating a JPA (JPQL) query for finding orders
	 * awaiting stock allocation, according to the stock allocation parameters.
	 * @param parameters the stock allocation report's parameters
	 * @param withWarehouseRestriction applies warehouse restriction
	 * 
	 * @return the prepared statement to be sent to the report service
	 */
	PreparedStatement buildPreparedStatement(StockAllocationParameters parameters, boolean withWarehouseRestriction);
	
}
