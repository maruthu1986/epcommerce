/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.reporting.ordersummary.services.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.formatting.TimeZoneInfo;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.reporting.ordersummary.OrderSummaryPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.ordersummary.impl.OrderSummaryPreparedStatementBuilderImpl;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.service.reporting.ReportService;

/**
 * Local OrderSummaryReportService. This service is a wrapper for calling the ReportService,
 * because BIRT's javascript engine does not handle Spring proxy beans well.
 *
 */
public class OrderSummaryReportServiceImpl {

	private static final Logger LOG = Logger.getLogger(OrderSummaryReportServiceImpl.class.getName());

	private ReportService reportService;

	/** Order query result columns.
	 *  Column 0 - order number which is used only for debugging
	 */
	private static final int DB_ORDER_DATE = 1;
	private static final int DB_ORDER_TOTAL = 2;
	private static final int DB_ITEM_COUNT = 3;
	private static final int DB_LIST_PRICE = 4;
	private static final int DB_PURCHASE_PRICE = 5;
	private static final int DB_ITEM_TAX = 6;

	/** Shipping query result columns.
	 *  Column 0 - order number which is used only for debugging
	 */
	private static final int DB_SHIPPING_ORDER_DATE = 1;
	private static final int DB_SHIPPING_COST = 2;
	private static final int DB_SHIPPING_TAX = 3;

	/** Grouped shipping result columns. */
	private static final int SHIPPING_COST = 0;
	private static final int SHIPPING_TAX = 1;
	private static final int SHIPPING_ROW_SIZE = 2;

	/** Grouped order result columns that are sent to BIRT. */
	private static final int REPORT_ORDER_DAY = 0;
	private static final int REPORT_ORDER_COUNT = 1;
	private static final int REPORT_ITEM_COUNT = 2;
	private static final int REPORT_ORDER_TOTAL = 3;
	private static final int REPORT_YEAR_MONTH = 4;
	private static final int REPORT_LIST_PRICE = 5;
	private static final int REPORT_UNIT_PRICE = 6;
	private static final int REPORT_TAX_TOTAL = 7;
	private static final int REPORT_SHIPPING = 8;
	private static final int REPORT_ROW_SIZE = 9;

	/**
	 * This method is called by BIRT Report and should return list of Object[].
	 *
	 * Array of Objects should contain next values:
	 * <ol>
	 * <li>Order Date</li>
	 * <li>Number of Orders</li>
	 * <li>Number of Items</li>
	 * <li>Order Total</li>
	 * <li>Order Year Month</li>
	 * <li>List Price</li>
	 * <li>Purchase Price</li>
	 * <li>Order Taxes</li>
	 * <li>Order Shipping</li>
	 * </ol>
	 *
	 * @return a list of Object[] that contain summary info about orders by date.
	 */
	public List<Object[]> orderSummaryReport() {
		final OrderSummaryPreparedStatementBuilder queryBuilder = new OrderSummaryPreparedStatementBuilderImpl();

		try {
			// Get all orders without shipping info
			final JpqlQueryBuilder orderInfoBuilder = queryBuilder.getOrderSummaryInfoQueryAndParams();
			final List<Object[]> orderResults = getReportService().execute(orderInfoBuilder.toString(),
					orderInfoBuilder.getParameterList().toArray());

			// Get shipping info for physical orders
			final JpqlQueryBuilder shippingInfoBuilder = queryBuilder.getShippingSummaryInfoQueryAndParams();
			final List<Object[]> shippingResults = getReportService().execute(shippingInfoBuilder.toString(),
					shippingInfoBuilder.getParameterList().toArray());

			// Group orders by day and add shipping info
			final List<Object[]> groupedOrders = groupOrdersByDay(orderResults);
			final Map<String, Object[]> groupedShipping = groupShippingInfoByDay(shippingResults);
			enrichOrdersWithShippingInfo(groupedOrders, groupedShipping);

			return groupedOrders;

		} catch (Exception ex) {
			LOG.error("Error executing report queries", ex);
			throw ex;
		}
	}


	/**
	 * Groups the order query results by day.
	 *
	 * @param orderResults The orders to group.
	 * @return groupedOrders The grouped orders.
	 */
	protected List<Object[]> groupOrdersByDay(final List<Object[]> orderResults) {
		final List<Object[]> reportList = new ArrayList<>();

		String currDay = "";
		Object[] reportRow = null;

		// Iterate through all orders
		for (Object[] orderResult : orderResults) {
			final String orderDay = formatReportDay((Date) orderResult[DB_ORDER_DATE]);

			// Control break - create new date row
			if (!orderDay.equals(currDay)) {
				currDay = orderDay;
				reportRow = new Object[REPORT_ROW_SIZE];
				reportList.add(reportRow);
				reportRow[REPORT_ORDER_DAY] = orderDay;
				reportRow[REPORT_ORDER_COUNT] = 0;
				reportRow[REPORT_ITEM_COUNT] = 0;
				reportRow[REPORT_ORDER_TOTAL] = BigDecimal.ZERO;
				reportRow[REPORT_YEAR_MONTH] = formatReportMonth((Date) orderResult[DB_ORDER_DATE]);
				reportRow[REPORT_LIST_PRICE] = BigDecimal.ZERO;
				reportRow[REPORT_UNIT_PRICE] = BigDecimal.ZERO;
				reportRow[REPORT_TAX_TOTAL] = BigDecimal.ZERO;
				reportRow[REPORT_SHIPPING] = BigDecimal.ZERO;
			}
			
			// Add to date totals
			reportRow[REPORT_ORDER_COUNT] = ((Integer) reportRow[REPORT_ORDER_COUNT]) + 1;
			reportRow[REPORT_ITEM_COUNT] = ((Integer) reportRow[REPORT_ITEM_COUNT])
						+ ((Long) orderResult[DB_ITEM_COUNT]).intValue();
			reportRow[REPORT_ORDER_TOTAL] = ((BigDecimal) reportRow[REPORT_ORDER_TOTAL]).
						add((BigDecimal) orderResult[DB_ORDER_TOTAL]);
			reportRow[REPORT_LIST_PRICE] = ((BigDecimal) reportRow[REPORT_LIST_PRICE]).
						add((BigDecimal) orderResult[DB_LIST_PRICE]);
			reportRow[REPORT_UNIT_PRICE] = ((BigDecimal) reportRow[REPORT_UNIT_PRICE]).
						add((BigDecimal) orderResult[DB_PURCHASE_PRICE]);
			if (orderResult[DB_ITEM_TAX] != null) {
				reportRow[REPORT_TAX_TOTAL] = ((BigDecimal) reportRow[REPORT_TAX_TOTAL]).
						add((BigDecimal) orderResult[DB_ITEM_TAX]);
			}
		}

		return reportList;
	}

	/**
	 * Groupe order shipment query results by date.
	 *
	 * @param shippingResults order shipment query results
	 * @return map of shipment amounts and taxes by date.
	 */
	protected Map<String, Object[]> groupShippingInfoByDay(final List<Object[]> shippingResults) {

		final Map<String, Object[]> shippingMap = new HashMap<>();

		String currDay = "";
		Object[] shippingRow = null;

		// Iterate through all orders
		for (Object[] shippingResult : shippingResults) {
			final String orderDay = formatReportDay((Date) shippingResult[DB_SHIPPING_ORDER_DATE]);

			// Control break - create new date row
			if (!orderDay.equals(currDay)) {
				currDay = orderDay;
				shippingRow = new Object[SHIPPING_ROW_SIZE];
				shippingMap.put(orderDay, shippingRow);
				shippingRow[SHIPPING_COST] = BigDecimal.ZERO;
				shippingRow[SHIPPING_TAX] = BigDecimal.ZERO;
			}

			// Add to date totals
			shippingRow[SHIPPING_COST] = ((BigDecimal) shippingRow[SHIPPING_COST]).
					add((BigDecimal) shippingResult[DB_SHIPPING_COST]);
			shippingRow[SHIPPING_TAX] = ((BigDecimal) shippingRow[SHIPPING_TAX]).
					add((BigDecimal) shippingResult[DB_SHIPPING_TAX]);
		}

		return shippingMap;
	}

	/**
	 * Adds shipping amount and taxes to grouped order results.
	 *
	 * @param reportList the order results grouped by day
	 * @param shippingMap the results of the shipping info query
	 */
	protected void enrichOrdersWithShippingInfo(final List<Object[]> reportList, final Map<String, Object[]> shippingMap) {

		reportList.forEach(reportRow -> {

			final String orderDate = (String) reportRow[REPORT_ORDER_DAY];
			final Object[] shippingInfo = shippingMap.get(orderDate);
			if (shippingInfo != null) {
				reportRow[REPORT_SHIPPING] = ((BigDecimal) reportRow[REPORT_SHIPPING]).
						add((BigDecimal) shippingInfo[SHIPPING_COST]);
				reportRow[REPORT_TAX_TOTAL] = ((BigDecimal) reportRow[REPORT_TAX_TOTAL]).
						add((BigDecimal) shippingInfo[SHIPPING_TAX]);
			}
		});
	}

	/**
	 * Helper method that formats a date as defined by the default core plugin.
	 *
	 * @param date The date object for that row
	 * @return Default Core Plugin Date String
	 */
	protected String formatReportDay(final Date date) {
		return DateTimeUtilFactory.getDateUtil().formatAsDate(date);
	}

	private TimeZoneInfo getTimeZoneInfo() {
		return TimeZoneInfo.getInstance();
	}

	/**
	 * Helper method that formats the year/month for a date.
	 *
	 * @param date The date object for that row
	 * @return The year/month string.
	 */
	protected String formatReportMonth(final Date date) {
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM", Locale.ENGLISH);	//$NON-NLS-1$
		formatter.setTimeZone(getTimeZoneInfo().getTimezone());
		return formatter.format(date);
	}

	private ReportService getReportService() {
		if (reportService == null) {
			reportService = ServiceLocator.getService(ContextIdNames.REPORT_SERVICE);
		}
		return reportService;
	}

}
