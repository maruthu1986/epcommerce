/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersbystatus.impl;

import com.elasticpath.cmclient.reporting.common.PreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.ordersbystatus.OrdersByStatusPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.ordersbystatus.parameters.OrdersByStatusParameters;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;

/**
 * JPA query builder for the Orders Status Report.
 */
@SuppressWarnings("nls")
public class OrdersByStatusPreparedStatementBuilderImpl extends PreparedStatementBuilder
			implements OrdersByStatusPreparedStatementBuilder {

	private static final String O_ORDER_NUMBER = "o.orderNumber";
	
	/**
	 * Constructor for the Builder.
	 */
	public OrdersByStatusPreparedStatementBuilderImpl() {
		setParameters(OrdersByStatusParameters.getInstance());
	}
	

	@Override
	public JpqlQueryBuilder getOrderBaseInfoQueryAndParams() {
		final String selectFields = "o.orderNumber, o.createdDate, o.currency, o.storeCode, o.status, o.total, o.exchangeOrder, o.cmUserUID, "
				+ "sum(skus.listUnitPriceInternal * skus.quantityInternal), sum(skus.amount), "
				+ "sum(shipments.itemTax)";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderImpl", "o", selectFields);
		jpqlQueryBuilder.appendInnerJoin("o.shipments", "shipments");
		jpqlQueryBuilder.appendInnerJoin("shipments.shipmentOrderSkusInternal", "skus");

		jpqlQueryBuilder.appendGroupBy("o.orderNumber, o.createdDate, o.currency, o.storeCode, o.status, o.total, o.exchangeOrder, o.cmUserUID");
		jpqlQueryBuilder.appendOrderBy(O_ORDER_NUMBER, true);

		final JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		setWhereClauseAndParameters(whereGroup, O_CREATED_DATE);
		return jpqlQueryBuilder;
	}

	@Override
	public JpqlQueryBuilder getShippingInfoQueryAndParams() {
		final String selectFields = "o.orderNumber, sum(shipments.shippingCostInternal), sum(shipments.shippingTax)";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("PhysicalOrderShipmentImpl", "shipments", selectFields);
		jpqlQueryBuilder.appendInnerJoin("shipments.orderInternal", "o");

		jpqlQueryBuilder.appendGroupBy("o.orderNumber");
		jpqlQueryBuilder.appendOrderBy(O_CREATED_DATE, true);

		final JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		setWhereClauseAndParameters(whereGroup,  O_CREATED_DATE);
		return jpqlQueryBuilder;
	}

	@Override
	public JpqlQueryBuilder getReturnInfoQueryAndParams() {
		final String selectFields = "o.orderNumber, r.rmaCode";

		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderReturnImpl", "r", selectFields);
		jpqlQueryBuilder.appendInnerJoin("r.order", "o");

		JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		addWhereStartDateClause(whereGroup, "r.createdDate");
		setWhereClauseAndParameters(whereGroup,  O_CREATED_DATE);
		return jpqlQueryBuilder;
	}

	@Override
	public JpqlQueryBuilder getCustomerInfoQueryAndParams() {
		final String selectFields = "o.orderNumber, c.uidPk, c.userId, cpv.localizedAttributeKey, cpv.shortTextValue";

		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderImpl", "o", selectFields);
		jpqlQueryBuilder.appendInnerJoin("o.customer", "c");
		jpqlQueryBuilder.appendInnerJoin("c.profileValueMap", "cpv");

		JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		setWhereClauseAndParameters(whereGroup, O_CREATED_DATE);
		return jpqlQueryBuilder;
	}

	/**
	 * Sets the where clauses for order queries.
	 *
	 * @param whereGroup the whereGroup object for that querybuilder object
	 * @param dateField the date field
	 */
	protected void setWhereClauseAndParameters(final JpqlQueryBuilderWhereGroup whereGroup, final String dateField) {

		super.setWhereClauseAndParameters(whereGroup, dateField, O_STORE_CODE);
		
		OrdersByStatusParameters params = (OrdersByStatusParameters) getParameters();
		
		if (params.getCurrency() != null) {
			whereGroup.appendWhereEquals("o.currency", params.getCurrency());
		}
		if (params.isShowExchangeOnly()) {
			whereGroup.appendWhereEquals("o.exchangeOrder", true);
		}
		whereGroup.appendWhereInCollection("o.status", params.getCheckedOrderStatuses());
	}

	/**
	 * Set an additional where clause on start date.
	 *
	 * @param whereGroup the querybuilder where group
	 * @param dateField the date field name
	 */
	protected void addWhereStartDateClause(final JpqlQueryBuilderWhereGroup whereGroup, final String dateField) {

		OrdersByStatusParameters params = (OrdersByStatusParameters) getParameters();
		whereGroup.appendWhere(dateField, ">=", params.getStartDate(), JpqlQueryBuilderWhereGroup.JpqlMatchType.AS_IS);
	}
}
