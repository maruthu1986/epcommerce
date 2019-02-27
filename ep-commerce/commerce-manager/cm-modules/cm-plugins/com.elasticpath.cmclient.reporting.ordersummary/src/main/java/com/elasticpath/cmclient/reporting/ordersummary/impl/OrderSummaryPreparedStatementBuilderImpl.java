/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.reporting.ordersummary.impl;

import com.elasticpath.cmclient.reporting.common.PreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.ordersummary.OrderSummaryPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.ordersummary.parameters.OrderSummaryParameters;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;

/**
 * JPA implementation for the Order Summary Report.
 */
@SuppressWarnings("nls")
public class OrderSummaryPreparedStatementBuilderImpl extends PreparedStatementBuilder implements
			OrderSummaryPreparedStatementBuilder {
	
	private static final String CREATED_DATE = "o.createdDate"; //$NON-NLS-1$

	/**
	 * Constructor for the Builder.
	 */
	public OrderSummaryPreparedStatementBuilderImpl() {
		setParameters(OrderSummaryParameters.getInstance());
	}

	@Override
	public JpqlQueryBuilder getOrderSummaryInfoQueryAndParams() {
		final String selectFields = "o.orderNumber, o.createdDate, o.total, sum(skus.quantityInternal), "
								+ "sum(skus.listUnitPriceInternal * skus.quantityInternal), sum(skus.amount), "
								+ "sum(shipments.itemTax)";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderImpl", "o", selectFields);	
		jpqlQueryBuilder.appendInnerJoin("o.shipments", "shipments"); 
		jpqlQueryBuilder.appendInnerJoin("shipments.shipmentOrderSkusInternal", "skus");
		jpqlQueryBuilder.appendGroupBy("o.orderNumber, o.createdDate, o.total");
		jpqlQueryBuilder.appendOrderBy(CREATED_DATE, true);
		
		final JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();	
		setWhereClauseAndParameters(jpqlQueryBuilder, whereGroup);
		return jpqlQueryBuilder;
	}

	@Override
	public JpqlQueryBuilder getShippingSummaryInfoQueryAndParams() {
		final String selectFields = "o.orderNumber, o.createdDate, sum(shipments.shippingCostInternal), sum(shipments.shippingTax)";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("PhysicalOrderShipmentImpl", "shipments", selectFields);
		jpqlQueryBuilder.appendInnerJoin("shipments.orderInternal", "o");
		jpqlQueryBuilder.appendGroupBy("o.orderNumber, o.createdDate");
		jpqlQueryBuilder.appendOrderBy(CREATED_DATE, true);

		final JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		setWhereClauseAndParameters(jpqlQueryBuilder, whereGroup);
		return jpqlQueryBuilder;
	}

	/**
	 * {@inheritDoc}
	 */
	public JpqlQueryBuilder getTaxesPerOrderQueryAndParams() {
		final String selectFields = "o.orderNumber, sum(t.taxAmount)";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderImpl", "o", selectFields);
		jpqlQueryBuilder.appendInnerJoin("o.shipments", "os");
		jpqlQueryBuilder.appendInnerJoin("TaxJournalRecordImpl", "t",
				"t.documentId = os.taxDocumentIdInternal");
		jpqlQueryBuilder.appendGroupBy("o.orderNumber");
		JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		setWhereClauseAndParameters(jpqlQueryBuilder, whereGroup);
		whereGroup.appendWhereEquals("t.journalType", "purchase");
		return jpqlQueryBuilder;
	}
	
	private void setWhereClauseAndParameters(final JpqlQueryBuilder jpqlQueryBuilder, 
			final JpqlQueryBuilderWhereGroup whereGroup) {
		OrderSummaryParameters parameters = (OrderSummaryParameters) getParameters();
		super.setWhereClauseAndParameters(whereGroup, O_CREATED_DATE, O_STORE_CODE);
		
		if (parameters.isShowExchangeOnly()) {
			jpqlQueryBuilder.appendInnerJoin("o.returns", "r"); 
			whereGroup.appendWhereEquals("r.returnType", OrderReturnType.EXCHANGE); 
		}
		if (parameters.getCurrency() != null) {
			whereGroup.appendWhereEquals("o.currency", parameters.getCurrency());
		}
		
		whereGroup.appendWhereInCollection("o.status", parameters.getCheckedOrderStatuses()); //$NON-NLS-1$
	}
}
