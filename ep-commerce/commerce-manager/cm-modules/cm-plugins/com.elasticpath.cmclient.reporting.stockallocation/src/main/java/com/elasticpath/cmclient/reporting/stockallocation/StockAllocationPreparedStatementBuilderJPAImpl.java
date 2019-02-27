/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.stockallocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.elasticpath.cmclient.reporting.common.PreparedStatement;
import com.elasticpath.cmclient.reporting.stockallocation.parameters.StockAllocationParameters;
import com.elasticpath.domain.catalog.AvailabilityCriteria;

/**
 * Builds a JPA (JPQL) prepared statement for retrieving orders awaiting stock
 * allocation.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class StockAllocationPreparedStatementBuilderJPAImpl implements StockAllocationPreparedStatementBuilder {

	/** Restricts skus by inventories. */
	private static final String SKU_CODE_EQUIJOIN = " AND i.skuCode = psku.skuCodeInternal"; //$NON-NLS-1$

	/** ORDER BY skuCode and CreatedDate. */
	private static final String ORDERS_UNALLOCATED_BASE	= " ORDER BY osk.skuCode, o.createdDate"; //$NON-NLS-1$
	
	/** Criteria sql for orders that have SKU's on pre-order or back-order. */
	private static final String ORDERS_UNALLOCATED_INVENTORY
		= "SELECT o.orderNumber, o.createdDate, osk, "  //$NON-NLS-1$
		+ "CURRENT_DATE, c.uidPk, o.billingAddress.firstName, o.billingAddress.lastName, c.userId " //$NON-NLS-1$
		+ "FROM OrderImpl AS o, StoreImpl as s "  //$NON-NLS-1$
		+ "INNER JOIN o.customer AS c " //$NON-NLS-1$
		+ "INNER JOIN o.shipments AS pos " //$NON-NLS-1$
		+ "INNER JOIN pos.shipmentOrderSkusInternal AS osk " //$NON-NLS-1$
		+ ", ProductSkuImpl AS psku " //$NON-NLS-1$"
		+ "INNER JOIN psku.productInternal p " //$NON-NLS-1$
		+ "WHERE o.status <> 'FAILED' " //$NON-NLS-1$
		+ "AND osk.skuCode = psku.skuCodeInternal " //$NON-NLS-1$
		+ "AND o.storeCode = s.code "; //$NON-NLS-1$
	
	/** Criteria sql for orders that have SKU's on pre-order or back-order with warehouse restriction. */
	private static final String ORDERS_UNALLOCATED_INVENTORY_WITH_WAREHOUSE
		= "SELECT o.orderNumber, o.createdDate, osk , "  //$NON-NLS-1$
		+ "i.restockDate, c.uidPk, o.billingAddress.firstName, o.billingAddress.lastName, c.userId " //$NON-NLS-1$
		+ "FROM OrderImpl AS o, InventoryImpl as i, StoreImpl as s "  //$NON-NLS-1$
		+ "INNER JOIN o.customer AS c " //$NON-NLS-1$
		+ "INNER JOIN o.shipments AS pos " //$NON-NLS-1$
		+ "INNER JOIN pos.shipmentOrderSkusInternal AS osk " //$NON-NLS-1$
		+ ", ProductSkuImpl AS psku " //$NON-NLS-1$
		+ "INNER JOIN psku.productInternal p " //$NON-NLS-1$
		+ "WHERE o.status <> 'FAILED' " //$NON-NLS-1$
		+ "AND osk.skuCode = psku.skuCodeInternal " //$NON-NLS-1$
		+ "AND o.storeCode = s.code " //$NON-NLS-1$
		+ "AND osk.skuCode = i.skuCode"; //$NON-NLS-1$
	
	/** The stores portion of the generated query. */
	private static final String PART_STORES = "s.name IN (:list)"; //$NON-NLS-1$
	/** The skuCode portion of the generated query. */
	private static final String PART_SKUCODE = "osk.skuCode = ?"; //$NON-NLS-1$
	/** The preorder/backorder portion of the generated query - same for both. */
	private static final String PART_PREORDER_BACKORDER = "p.availabilityCriteriaInternal = ?"; //$NON-NLS-1$
	/** AND. */
	private static final String AND = " AND "; //$NON-NLS-1$
	/** Warehouses restricted to store warehouses. **/
	private static final String PART_WAREHOUSE_RESTRICTION = "i.warehouseUid IN (SELECT s.warehouses.uidPk FROM StoreImpl as s " //$NON-NLS-1$
			+ "WHERE s.code = c.storeCode)"; //$NON-NLS-1$
	/** Including the results that have no inventory records. **/
	private static final String PART_WAREHOUSES_IS_NULL = "i.warehouseUid IS NULL"; //$NON-NLS-1$
	/** OR. */
	@SuppressWarnings("PMD.ShortVariable")
	private static final String OR = " OR "; //$NON-NLS-1$
	/** (. */
	private static final String OPEN_BRACKET = "("; //$NON-NLS-1$
	/** ). */
	private static final String CLOSE_BRACKET = ")"; //$NON-NLS-1$
	
	/**
	 * Builds the prepared statement, creating a JPA (JPQL) query for finding orders
	 * awaiting stock allocation, according to the stock allocation parameters.
	 * @param parameters the stock allocation report's parameters
	 * @param withWarehouseRestriction applies warehouse restriction
	 * @return the prepared statement to be sent to the report service
	 */
	@SuppressWarnings("PMD.CyclomaticComplexity")
	public PreparedStatement buildPreparedStatement(final StockAllocationParameters parameters, final boolean withWarehouseRestriction) {
		PreparedStatement preparedStatement = new PreparedStatement();
		StringBuffer query;
		
		if (withWarehouseRestriction) {
			query = new StringBuffer(ORDERS_UNALLOCATED_INVENTORY_WITH_WAREHOUSE);
		} else {
			query = new StringBuffer(ORDERS_UNALLOCATED_INVENTORY);
		}
		
		//StoreNames
		Collection<String> storeNames = parameters.getStoreNames();
		if (!(CollectionUtils.isEmpty(storeNames))) {
			query.append(AND).append(PART_STORES);
			preparedStatement.setCollection(storeNames);
		}
		
		List<Object> parameterList = new ArrayList<Object>();
		int numParameters = 1;
		//SkuCode
		String skuCode = parameters.getSkuCode();
		if (!StringUtils.isBlank(skuCode)) {
			query.append(AND);
			query.append(PART_SKUCODE).append(numParameters);
			parameterList.add(skuCode);
			numParameters++;
		}
		//SkuAvailability
		int skuAvailRule = parameters.getSkuAvailRule();
		if (skuAvailRule != 0) {
			query.append(AND);
			appendPreBackOrderRestriction(query, parameterList, numParameters, skuAvailRule);
			if (withWarehouseRestriction) {
				appendWarehouseRestriction(query);
			}
		}
		//ORDER BY
		query.append(ORDERS_UNALLOCATED_BASE);
		
		preparedStatement.setQueryString(query.toString(), "list"); //$NON-NLS-1$
		preparedStatement.setCollection(storeNames);
		preparedStatement.setParameters(parameterList);
		
		return preparedStatement;
	}

	private void appendPreBackOrderRestriction(final StringBuffer query,	final List<Object> parameterList, final int numParameters, 
			final int skuAvailRule) {
		int numParams = numParameters;
		if (skuAvailRule == StockAllocationParameters.AVAIL_BACK_ORDER_ONLY) {
			query.append(PART_PREORDER_BACKORDER).append(numParams);
			parameterList.add(AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER);
		} else if (skuAvailRule == StockAllocationParameters.AVAIL_PRE_ORDER_ONLY) {
			query.append(PART_PREORDER_BACKORDER).append(numParams);
			parameterList.add(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER);
		} else if (skuAvailRule == StockAllocationParameters.AVAIL_PRE_BACK_ORDER) {
			query.append(OPEN_BRACKET);
			query.append(PART_PREORDER_BACKORDER).append(numParams);
			numParams++;
			query.append(OR).append(PART_PREORDER_BACKORDER).append(numParams);
			query.append(CLOSE_BRACKET);
			parameterList.add(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER);
			parameterList.add(AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER);
		}
	}

	private void appendWarehouseRestriction(final StringBuffer query) {
		query.append(SKU_CODE_EQUIJOIN);
		query.append(AND);
		query.append(OPEN_BRACKET);
		query.append(PART_WAREHOUSE_RESTRICTION);
		query.append(OR);
		query.append(PART_WAREHOUSES_IS_NULL);
		query.append(CLOSE_BRACKET);
	}
}
