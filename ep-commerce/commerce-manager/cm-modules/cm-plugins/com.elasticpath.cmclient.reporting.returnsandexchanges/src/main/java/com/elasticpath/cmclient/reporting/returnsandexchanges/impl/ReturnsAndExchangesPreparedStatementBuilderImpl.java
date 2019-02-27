/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges.impl;

import java.util.Set;

import com.elasticpath.cmclient.reporting.common.PreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.returnsandexchanges.ReturnsAndExchangesPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.returnsandexchanges.parameters.ReturnsAndExchangesParameters;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;

/**
 * JPA implementation for {@link ReturnsAndExchangesPreparedStatementBuilder}.
 */
@SuppressWarnings("nls")
public class ReturnsAndExchangesPreparedStatementBuilderImpl extends PreparedStatementBuilder
			implements ReturnsAndExchangesPreparedStatementBuilder {
	
	/**
	 * Constructor for the Builder.
	 */
	public ReturnsAndExchangesPreparedStatementBuilderImpl() {
		setParameters(ReturnsAndExchangesParameters.getInstance());
	}

	@Override
	public JpqlQueryBuilder getReturnInfoPerSkuQueryAndParams() {
		final String selectFields = "orsku, orr, orr.createdByCmUser";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderReturnImpl", "orr", selectFields);
		jpqlQueryBuilder.appendInnerJoin("orr.order", "o"); //many-to-one
		jpqlQueryBuilder.appendInnerJoin("orr.orderReturnSkus", "orsku");
		
		final JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();	
		setWhereClauseAndParameters(whereGroup, ORR_CREATED_DATE);

		jpqlQueryBuilder.appendOrderBy("o.orderNumber", true);
		jpqlQueryBuilder.appendOrderBy("orr.rmaCode", true);

		return jpqlQueryBuilder;
	}
	
	private void setWhereClauseAndParameters(final JpqlQueryBuilderWhereGroup whereGroup, final String dateField) {
		super.setWhereClauseAndParameters(whereGroup, dateField, O_STORE_CODE);
		final ReturnsAndExchangesParameters params = (ReturnsAndExchangesParameters) getParameters();
		whereGroup.appendWhereInCollection("orr.returnStatus", params.getCheckedOrderReturnStatuses());
		whereGroup.appendWhereEquals("o.currency", params.getCurrency());
		if (params.getRmaType() != null) {
			whereGroup.appendWhereEquals("orr.returnType", params.getRmaType());
		}
	}

	@Override
	public JpqlQueryBuilder getTaxesPerReturnQueryAndParams(final Set<String> taxDocumentIds) {
		final ReturnsAndExchangesParameters params = (ReturnsAndExchangesParameters) getParameters();

		final String selectFields = "t.documentId, sum(t.taxAmount)";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("TaxJournalRecordImpl", "t", selectFields);
		jpqlQueryBuilder.appendGroupBy("t.documentId");

		JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		whereGroup.appendWhereInCollection("t.documentId", taxDocumentIds);
		whereGroup.appendWhereEquals("t.currency", params.getCurrency().getCurrencyCode());
		return jpqlQueryBuilder;
	}

}
