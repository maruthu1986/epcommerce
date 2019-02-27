/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersbystatus.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.reporting.ordersbystatus.OrdersByStatusPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.ordersbystatus.OrdersByStatusReportMessages;
import com.elasticpath.cmclient.reporting.ordersbystatus.impl.OrdersByStatusPreparedStatementBuilderImpl;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.service.reporting.ReportService;

/**
 * Local OrdersByStatusReportService.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "restriction", "nls" })
public class OrdersByStatusReportServiceImpl {

	private static final Logger LOG = Logger.getLogger(OrdersByStatusReportServiceImpl.class.getName());

	private ReportService reportService;

	/** Report columns. */
	private static final int REPORT_STATUS = 0;
	private static final int REPORT_ORDER_NUMBER = 1;
	private static final int REPORT_CREATED_DATE = 2;
	private static final int REPORT_STORE = 3;
	private static final int REPORT_CURRENCY = 4;
	private static final int REPORT_TOTAL = 5;
	private static final int REPORT_RETURN_FLAG = 6;
	private static final int REPORT_LIST_PRICE = 7;
	private static final int REPORT_PURCHASE_PRICE = 8;
	private static final int REPORT_DISC_PRICE = 9;
	private static final int REPORT_SHIPPING_COST = 10;
	private static final int REPORT_TAX_TOTAL = 11;
	private static final int REPORT_EXCHANGE_ORDER = 12;
	private static final int REPORT_CUSTOMER_ID = 13;
	private static final int REPORT_CUSTOMER_LAST_NAME = 14;
	private static final int REPORT_CUSTOMER_FIRST_NAME = 15;
	private static final int REPORT_CUSTOMER_EMAIL = 16;
	private static final int REPORT_CREATED_BY_CSR = 17;
	private static final int REPORT_ROW_LENGTH = 18;

	/**
	 * This method is called by BIRT and returns a List of Object[] with columns as defined above.
	 * 
	 * @return a list of Object[] that contain one row per order..
	 */
	public List<Object[]> orderDataSet() {

		final OrdersByStatusPreparedStatementBuilder queryBuilder = new OrdersByStatusPreparedStatementBuilderImpl();

		try {
			// Get base order info - one row per order
			final List<Object[]> reportList = getBaseOrderInfo(queryBuilder);

			// Create hash map into reportList with orderNumber as the key
			final Map<String, Object[]> reportMap = new HashMap<>();
			reportList.forEach(reportRow -> reportMap.put((String) reportRow[REPORT_ORDER_NUMBER], reportRow));

			// Enrich the reportList
			enrichWithShippingInfo(queryBuilder, reportMap);
			enrichWithReturnInfo(queryBuilder, reportMap);
			enrichWithCustomerInfo(queryBuilder, reportMap);

			return reportList;

		} catch (Exception ex) {
			LOG.error("Error executing report queries", ex);
			throw ex;
		}
	}

	/**
	 * Get base order information.
	 *
	 * @param queryBuilder the query builder
	 * @return List of Object[] with one row per order
	 */
	protected List<Object[]> getBaseOrderInfo(final OrdersByStatusPreparedStatementBuilder queryBuilder) {

		final int orderNumberIndex = 0;
		final int createdDateIndex = 1;
		final int currencyIndex = 2;
		final int storeCodeIndex = 3;
		final int orderStatusIndex = 4;
		final int totalPriceIndex = 5;
		final int exchangeOrderIndex = 6;
		final int cmUserUidIndex = 7;
		final int listPriceIndex = 8;
		final int purchasePriceIndex = 9;
		final int itemTaxIndex = 10;

		final String enDash = "\u2013";	// Unicode en-dash

		// Get order base results - one row per order
		final JpqlQueryBuilder orderBuilder = queryBuilder.getOrderBaseInfoQueryAndParams();
		final List<Object[]> orderResults = getReportService().execute(orderBuilder.toString(),
				orderBuilder.getParameterList().toArray());

		// Transform into report list
		final ArrayList<Object[]> reportList = new ArrayList<>();
		reportList.ensureCapacity(orderResults.size());

		for (Object[] orderResult : orderResults) {
			Object[] reportRow = new Object[REPORT_ROW_LENGTH];
			reportList.add(reportRow);

			reportRow[REPORT_ORDER_NUMBER] = orderResult[orderNumberIndex];
			reportRow[REPORT_CREATED_DATE] = formatDate((Date) orderResult[createdDateIndex]);
			final OrderStatus orderStatus = OrderStatus.valueOf(orderResult[orderStatusIndex].toString());
			reportRow[REPORT_STATUS] = OrdersByStatusReportMessages.get().getLocalizedName(orderStatus);
			reportRow[REPORT_STORE] = orderResult[storeCodeIndex];
			reportRow[REPORT_CURRENCY] = ((Currency) orderResult[currencyIndex]).getCurrencyCode();
			reportRow[REPORT_CREATED_BY_CSR] = (orderResult[cmUserUidIndex] == null ? enDash : OrdersByStatusReportMessages.get().yes_as_string);
			reportRow[REPORT_TOTAL] = orderResult[totalPriceIndex];
			reportRow[REPORT_DISC_PRICE] = null; // unused
			reportRow[REPORT_PURCHASE_PRICE] = orderResult[purchasePriceIndex];
			reportRow[REPORT_LIST_PRICE] = orderResult[listPriceIndex];
			reportRow[REPORT_SHIPPING_COST] = BigDecimal.ZERO;
			reportRow[REPORT_TAX_TOTAL] = orderResult[itemTaxIndex];
			reportRow[REPORT_RETURN_FLAG] = enDash;
			reportRow[REPORT_EXCHANGE_ORDER] = ((Boolean) orderResult[exchangeOrderIndex]
					? OrdersByStatusReportMessages.get().yes_as_string : enDash);
			reportRow[REPORT_CUSTOMER_ID] = "";
			reportRow[REPORT_CUSTOMER_LAST_NAME] = "";
			reportRow[REPORT_CUSTOMER_FIRST_NAME] = "";
			reportRow[REPORT_CUSTOMER_EMAIL] = "";
		}
		return reportList;
	}

	/**
	 * Enrich base order information with shipping cost and taxes.
	 *
	 * @param queryBuilder the query builder
	 * @param reportMap the report map
	 */
	protected void enrichWithShippingInfo(final OrdersByStatusPreparedStatementBuilder queryBuilder, final Map<String, Object[]> reportMap) {

		final int orderNumberIndex = 0;
		final int shippingCostIndex = 1;
		final int shippingTaxIndex = 2;

		// Get order shipping info from physical shipments - one row per order
		final JpqlQueryBuilder shippingBuilder = queryBuilder.getShippingInfoQueryAndParams();
		final List<Object[]> shippingResults = getReportService().execute(shippingBuilder.toString(),
				shippingBuilder.getParameterList().toArray());

		for (Object[] shippingResult : shippingResults) {
			final Object[] reportRow = reportMap.get(shippingResult[orderNumberIndex]);
			if (reportRow != null) {
				reportRow[REPORT_SHIPPING_COST] = ((BigDecimal) reportRow[REPORT_SHIPPING_COST]).
						add((BigDecimal) shippingResult[shippingCostIndex]);
				reportRow[REPORT_TAX_TOTAL] = ((BigDecimal) reportRow[REPORT_TAX_TOTAL]).
						add((BigDecimal) shippingResult[shippingTaxIndex]);
			}
		}
	}

	/**
	 * Enrich base order information with order return info.
	 *
	 * @param queryBuilder the query builder
	 * @param reportMap the report map
	 */
	protected void enrichWithReturnInfo(final OrdersByStatusPreparedStatementBuilder queryBuilder, final Map<String, Object[]> reportMap) {

		final int orderNumberIndex = 0;

		// Get return info - one row per order return
		final JpqlQueryBuilder returnBuilder = queryBuilder.getReturnInfoQueryAndParams();
		final List<Object[]> returnResults = getReportService().execute(returnBuilder.toString(),
				returnBuilder.getParameterList().toArray());

		for (Object[] returnResult: returnResults) {
			final Object[] reportRow = reportMap.get(returnResult[orderNumberIndex]);
			if (reportRow != null) {
				reportRow[REPORT_RETURN_FLAG] = OrdersByStatusReportMessages.get().yes_as_string;
			}
		}
	}

	/**
	 * Enrich base order information with customer info.
	 *
	 * @param queryBuilder the query builder
	 * @param reportMap the report map
	 */
	protected void enrichWithCustomerInfo(final OrdersByStatusPreparedStatementBuilder queryBuilder, final Map<String, Object[]> reportMap) {

		final int orderNumberIndex = 0;
		final int customerUidIndex = 1;
		final int attributeKeyIndex = 3;
		final int attributeValueIndex = 4;

		// Get customer info - one row per customer attribute
		final JpqlQueryBuilder customerBuilder = queryBuilder.getCustomerInfoQueryAndParams();
		final List<Object[]> customerResults = getReportService().execute(customerBuilder.toString(),
				customerBuilder.getParameterList().toArray());

		for (Object[] customerResult : customerResults) {
			final Object[] reportRow = reportMap.get(customerResult[orderNumberIndex]);
			if (reportRow != null) {
				reportRow[REPORT_CUSTOMER_ID] = ((Long) customerResult[customerUidIndex]).toString();
				if ("CP_LAST_NAME".equals((String) customerResult[attributeKeyIndex])) {
					reportRow[REPORT_CUSTOMER_LAST_NAME] = customerResult[attributeValueIndex];
				} else if ("CP_FIRST_NAME".equals((String) customerResult[attributeKeyIndex])) {
					reportRow[REPORT_CUSTOMER_FIRST_NAME] = customerResult[attributeValueIndex];
				} else if ("CP_EMAIL".equals((String) customerResult[attributeKeyIndex])) {
					reportRow[REPORT_CUSTOMER_EMAIL] = customerResult[attributeValueIndex];
				}
			}
		}
	}

	private ReportService getReportService() {
		if (reportService == null) {
			reportService = ServiceLocator.getService(ContextIdNames.REPORT_SERVICE);
		}
		return reportService;
	}

	private String formatDate(final Date date) {
		return DateTimeUtilFactory.getDateUtil().formatAsDateTime(date);
	}
}
