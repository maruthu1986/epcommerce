/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.reporting.returnsandexchanges.ReturnsAndExchangesPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.returnsandexchanges.ReturnsAndExchangesReportMessages;
import com.elasticpath.cmclient.reporting.returnsandexchanges.impl.ReturnsAndExchangesPreparedStatementBuilderImpl;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnSkuReason;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.service.reporting.ReportService;

/**
 * Local ReturnsAndExchangesReportService.
 */
public class ReturnsAndExchangesReportServiceImpl {

	private static final Logger LOG = Logger.getLogger(ReturnsAndExchangesReportServiceImpl.class.getName());

	// Order return sku query result indexes
	private static final int DB_ORDER_RETURN_SKU_INDEX = 0;
	private static final int DB_ORDER_RETURN_INDEX = 1;
	private static final int DB_RETURN_CMUSER_INDEX = 2;

	// Tax query result indexes
	private static final int DB_TAX_DOCUMENT_INDEX = 0;
	private static final int DB_TAX_AMOUNT_INDEX = 1;

	// Report row array indexes
	private static final int RETURN_STATUS_INDEX = 0;
	private static final int ORDER_NUMBER_INDEX = 1;
	private static final int RETURN_CODE_INDEX = 2;
	private static final int RETURN_DATE_INDEX = 3;
	private static final int RETURN_AMOUNT_INDEX = 4;
	private static final int RETURN_TAX_INDEX = 5;
	private static final int RETURN_SHIPPING_INDEX = 6;
	private static final int RETURN_REASON_INDEX = 7;
	private static final int CREATED_BY_INDEX = 8;
	private static final int ORDER_CREATED_DATE_INDEX = 9;
	private static final int ORDER_TOTAL_INDEX = 10;
	private static final int ORDER_STATUS_INDEX = 11;
	private static final int CUSTOMER_UID = 12;
	private static final int CUSTOMER_LAST_NAME = 13;
	private static final int CUSTOMER_FIRST_NAME = 14;
	private static final int CUSTOMER_EMAIL = 15;
	private static final int RETURN_TYPE_INDEX = 16;
	private static final int EXCHANGE_ORDER_NUMBER_INDEX = 17;
	private static final int ORDER_RETURN_ROW_SIZE = 18;

	/**
	 * This method is called by BIRT Report and returns a list of Object[]
	 * as defined by the report row indexes above.
	 *
	 * @return a list of Object[] that contain report data.
	 */
	public List<Object[]> orderReturnDataSet() {
		try {
			ReturnsAndExchangesPreparedStatementBuilder builder = getPreparedStatementBuilder();

			// Get order return skus, along with associated order return, order, customer and cmuser objects
			JpqlQueryBuilder orderReturnSkuQuery = builder.getReturnInfoPerSkuQueryAndParams();
			final List<Object[]> orderReturnSkuResults = getReportService().execute(orderReturnSkuQuery.toString(),
					orderReturnSkuQuery.buildParameterList().toArray());

			// Get taxes
			Set<String> taxDocumentIds = extractTaxDocumentIds(orderReturnSkuResults);
			JpqlQueryBuilder returnTaxQuery = builder.getTaxesPerReturnQueryAndParams(taxDocumentIds);
			final List<Object[]> returnTaxResults = getReportService().execute(returnTaxQuery.toString(),
					returnTaxQuery.buildParameterList().toArray());

			// Create map of tax results
			final Map<String, Object[]> taxMap = new HashMap<>();
			for (Object[] taxResult : returnTaxResults) {
				taxMap.put((String) taxResult[DB_TAX_DOCUMENT_INDEX], taxResult);
			}

			return flattenOrderReturnResults(orderReturnSkuResults, taxMap);

		} catch (Exception ex) {
			LOG.error("Error executing report queries", ex);
			throw ex;
		}
	}

	/**
	 * Extracts the tax document ids from the query results. Each row is a return sku so there may be
	 * repeated document ids.
	 *
	 * @param dbResults the query result
	 * @return taxDocumentIds
	 */
	protected Set<String> extractTaxDocumentIds(final List<Object[]> dbResults) {
		Set<String> taxDocumentIds = new HashSet<>();
		for (Object[] dbResult : dbResults) {
			OrderReturn orderReturn = (OrderReturn) dbResult[DB_ORDER_RETURN_INDEX];
			taxDocumentIds.add(orderReturn.getTaxDocumentId().toString());
		}
		return taxDocumentIds;
	}

	/**
	 * For each return, there are one or more return skus and therefore one or more return reasons for each order.
	 * This method flattens those return reasons onto the same order return. 
	 * 
	 * @param orderReturnSkuResults the query results containing the order return skus
	 * @param taxMap map of order return taxes
	 * @return an list of flattened results
	 */
	private List<Object[]> flattenOrderReturnResults(final List<Object[]> orderReturnSkuResults,
													 final Map<String, Object[]> taxMap) {

		final OrderReturnSkuReason orderReturnSkuReasons = ServiceLocator.getService(ContextIdNames.ORDER_RETURN_SKU_REASON);
		final List<Object[]> reportList = new ArrayList<>();

		String currRmaCode = "";
		Object[] reportRow = null;
		Set<String> returnReasons = new HashSet<>();

		// Iterate through Order Return SKUs and attached objects
		for (Object[] orderReturnSkuResult : orderReturnSkuResults) {
			final OrderReturnSku orderReturnSku = (OrderReturnSku) orderReturnSkuResult[DB_ORDER_RETURN_SKU_INDEX];
			final OrderReturn orderReturn = (OrderReturn) orderReturnSkuResult[DB_ORDER_RETURN_INDEX];

			// Control break - create new order return row
			if (!orderReturn.getRmaCode().equals(currRmaCode)) {
				currRmaCode = orderReturn.getRmaCode();
				reportRow = createReportRow(orderReturnSkuResult, taxMap);
				reportList.add(reportRow);
				returnReasons.clear();
			}

			// Add to existing report row
			reportRow[RETURN_AMOUNT_INDEX] = ((BigDecimal) reportRow[RETURN_AMOUNT_INDEX]).
					add(orderReturnSku.getReturnAmount().negate());
			returnReasons.add(orderReturnSkuReasons.getReasonMap().get(orderReturnSku.getReturnReason()));
			final String reasons = returnReasons.toString();
			reportRow[RETURN_REASON_INDEX] = reasons.substring(1, reasons.length() - 1);
		}

		return reportList;
	}

	private Object[] createReportRow(final Object[] orderReturnSkuResult, final Map<String, Object[]> taxMap) {

		final Object[] reportRow = new Object[ORDER_RETURN_ROW_SIZE];

		final OrderReturn orderReturn = (OrderReturn) orderReturnSkuResult[DB_ORDER_RETURN_INDEX];
		final Order order = orderReturn.getOrder();
		final Customer customer = order.getCustomer();
		final CmUser cmUser = (CmUser) orderReturnSkuResult[DB_RETURN_CMUSER_INDEX];
		final Object[] taxRow = taxMap.get(orderReturn.getTaxDocumentId().toString());

		reportRow[RETURN_STATUS_INDEX] = ReturnsAndExchangesReportMessages.get().getLocalizedName(orderReturn.getReturnStatus());
		reportRow[RETURN_CODE_INDEX] = orderReturn.getRmaCode();
		reportRow[ORDER_NUMBER_INDEX] = order.getOrderNumber();
		reportRow[RETURN_DATE_INDEX] = formatDateTime(orderReturn.getCreatedDate());
		reportRow[RETURN_AMOUNT_INDEX] = BigDecimal.ZERO;
		reportRow[RETURN_TAX_INDEX] = (taxRow == null ? BigDecimal.ZERO : taxRow[DB_TAX_AMOUNT_INDEX]);
		reportRow[RETURN_SHIPPING_INDEX] = orderReturn.getShippingCost().negate();
		reportRow[RETURN_REASON_INDEX] = "";
		reportRow[CREATED_BY_INDEX] = (cmUser == null ? "" : (cmUser.getFirstName() + " " + cmUser.getLastName()));
		reportRow[ORDER_CREATED_DATE_INDEX] = formatDateTime(order.getCreatedDate());
		reportRow[ORDER_TOTAL_INDEX] = order.getTotal();
		reportRow[ORDER_STATUS_INDEX] = order.getStatus();
		reportRow[CUSTOMER_UID] = Long.toString(customer.getUidPk());
		reportRow[CUSTOMER_LAST_NAME] = customer.getLastName();
		reportRow[CUSTOMER_FIRST_NAME] = customer.getFirstName();
		reportRow[CUSTOMER_EMAIL] = customer.getEmail();
		switch (orderReturn.getReturnType()) {
			case RETURN:
				reportRow[RETURN_TYPE_INDEX] = ReturnsAndExchangesReportMessages.get().OrderReturnType_Return;
				reportRow[EXCHANGE_ORDER_NUMBER_INDEX] = "";
				break;
			case EXCHANGE:
				reportRow[RETURN_TYPE_INDEX] = ReturnsAndExchangesReportMessages.get().OrderReturnType_Exchange;
				reportRow[EXCHANGE_ORDER_NUMBER_INDEX] = orderReturn.getExchangeOrder().getOrderNumber();
				break;
			default:
				reportRow[RETURN_TYPE_INDEX] = "";
				reportRow[EXCHANGE_ORDER_NUMBER_INDEX] = "";
				break;
		}
		return reportRow;
	}

	private String formatDateTime(final Date createdDate) {
		return DateTimeUtilFactory.getDateUtil().formatAsDateTime(createdDate);
	}

	private ReportService getReportService() {
		return ServiceLocator.getService(ContextIdNames.REPORT_SERVICE);
	}

	private ReturnsAndExchangesPreparedStatementBuilder getPreparedStatementBuilder() {
		return new ReturnsAndExchangesPreparedStatementBuilderImpl();
	}
}
