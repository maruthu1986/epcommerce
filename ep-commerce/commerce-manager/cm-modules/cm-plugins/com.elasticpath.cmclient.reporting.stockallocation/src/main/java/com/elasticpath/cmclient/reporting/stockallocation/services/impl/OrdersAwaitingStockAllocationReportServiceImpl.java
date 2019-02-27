/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.stockallocation.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.common.PreparedStatement;
import com.elasticpath.cmclient.reporting.stockallocation.StockAllocationPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.stockallocation.StockAllocationPreparedStatementBuilderJPAImpl;
import com.elasticpath.cmclient.reporting.stockallocation.StockAllocationReportMessages;
import com.elasticpath.cmclient.reporting.stockallocation.StockAllocationReportSection;
import com.elasticpath.cmclient.reporting.stockallocation.parameters.StockAllocationParameters;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.reporting.ReportService;

/**
 * Local service wrapping remote report service call to retrieve
 * data to feed into the Birt report.
 *
 */
public class OrdersAwaitingStockAllocationReportServiceImpl {

	//Report index
	private static final int ROW_SIZE = 9;
	private static final int ORDER_NUM_INDEX = 0;
	private static final int ORDER_DATE_INDEX = 1;
	private static final int SKU_CODE_INDEX = 2;
	private static final int PRODUCT_NAME_INDEX = 3;
	private static final int QTY_ORDERED_INDEX = 4;
	private static final int RESTOCK_DATE_INDEX = 5;
	private static final int CUST_NUM_INDEX = 6;
	private static final int CUST_NAME_INDEX = 7;
	private static final int CUST_EMAIL_INDEX = 8;

	//DB Index
	private static final int DB_ORDER_NUM_INDEX = 0;
	private static final int DB_ORDER_DATE_INDEX = 1;
	private static final int DB_SKU_INDEX = 2;
	private static final int DB_RESTOCK_DATE = 3;
	private static final int DB_CUST_UIDPK_INDEX = 4;
	private static final int DB_CUST_FIRST_NAME_INDEX = 5;
	private static final int DB_CUST_LAST_NAME_INDEX = 6;
	private static final int DB_CUST_EMAIL_INDEX = 7;

	/**
	 * report all orders that have SKU's on pre-order or back-order. I.e. orders that have not yet been assigned inventory.
	 *
	 * @return object array in a list for birt report to process
	 */
	public List<Object[]> orderWithUnAllocatedStockReport() {
		StockAllocationParameters parameters = getParametersObject();
		PreparedStatement preparedStatementWithoutWarehouseRestriction = getPreparedStatementBuilder().buildPreparedStatement(parameters, false);
		final List<Object[]> ordersWithoutWarehouseRestriction = getReportService().executeWithList(
				preparedStatementWithoutWarehouseRestriction.getQueryString(), 
				preparedStatementWithoutWarehouseRestriction.getCollectionParameterName(),
				preparedStatementWithoutWarehouseRestriction.getCollection(),
				preparedStatementWithoutWarehouseRestriction.getParameters().toArray());
		PreparedStatement preparedStatementWithWarehouserestriction = getPreparedStatementBuilder().buildPreparedStatement(parameters, true);
		final List<Object[]> ordersWithWarehouseRestriction = getReportService().executeWithList(
				preparedStatementWithWarehouserestriction.getQueryString(), 
				preparedStatementWithWarehouserestriction.getCollectionParameterName(),
				preparedStatementWithWarehouserestriction.getCollection(),
				preparedStatementWithWarehouserestriction.getParameters().toArray());
		
		return generateReportCollection(union(ordersWithoutWarehouseRestriction, ordersWithWarehouseRestriction));
	}

	private List<Object[]> union(final List<Object[]> ordersWithoutWarehouseRestriction, final List<Object[]> ordersWithWarehouseRestriction) {
		final List<Object[]> result = new ArrayList<Object[]>();
		result.addAll(ordersWithWarehouseRestriction);
		
		for (Object[] orderRow : ordersWithoutWarehouseRestriction) {
			boolean orderLineExists = false;
			for (Object[] orderRow2 : ordersWithWarehouseRestriction) {
				if (orderLinesAreEqual(orderRow, orderRow2)) {
					orderLineExists = true;
				}
			}
			if (!orderLineExists) {
				setRestockDateBlank(orderRow);
				result.add(orderRow);
			}
		}
		
		return result;
	}
	
	private void setRestockDateBlank(final Object[] orderRow) {
		final int restockDateIdx = 5;
		orderRow[restockDateIdx] = StringUtils.EMPTY;
	}

	private boolean orderLinesAreEqual(final Object[] orderRow, final Object[] orderRow2) {
		return orderRow[0].equals(orderRow2[0]) && orderRow[2].equals(orderRow2[2]);
	}
	
	private ReportService getReportService() {
		return LoginManager.getInstance().getBean(ContextIdNames.REPORT_SERVICE);
	}
	
	/**
	 * @return the map of parameters for the StockAllocationReport.
	 */
	protected Map<String, Object> getParametersMap() {
		for (ReportType reportType : ReportTypeManager.getInstance().getReportTypes()) {
			if (reportType.getName().equalsIgnoreCase(StockAllocationReportMessages.report)) {
				return reportType.getReport().getParameters();
			}
		}
		throw new EpServiceException("Unable to retrieve the parameters for the StockAllocationReport."); //$NON-NLS-1$
	}
	
	/**
	 * Calls {@link #getParametersMap()} to get the map of parameters for this report, then
	 * uses the map to populate a new <code>StockAllocationParameters</code> object.
	 * @return the populated StockAllocationParameters
	 */
	protected StockAllocationParameters getParametersObject() {
		Map<String, Object> paramsMap = getParametersMap();
		StockAllocationParameters parameters = new StockAllocationParameters();
		Object skuCodeParam = paramsMap.get(StockAllocationReportSection.PARAMETER_SKUCODE);
		Object storeNamesParam = paramsMap.get(StockAllocationReportSection.PARAMETER_STORENAMES);
		Object availabilityParam = paramsMap.get(StockAllocationReportSection.PARAMETER_SKUAVAIL_RULE);
		if (skuCodeParam instanceof String) {
			parameters.setSkuCode((String) skuCodeParam);			
		}
		if (storeNamesParam instanceof List) {
			parameters.setStoreNames((List<String>) storeNamesParam);
		}
		if (availabilityParam instanceof Integer) {
			parameters.setSkuAvailRule((Integer) availabilityParam);
		}
		return parameters;
	}

	/**
	 * Generates the List of data Rows that is expected by the BIRT StockAllocationReport. 
	 * @param orders the list of orders returned from the report service
	 * @return the list of data rows for inclusion in the report
	 */
	protected List<Object[]> generateReportCollection(final List<Object[]> orders) {
		final List<Object[]> returnValues = new ArrayList<Object[]>(orders.size());
		
		for (int i = 0; i < orders.size(); i++) {
			
			Object[] order = orders.get(i);
			OrderSku sku = (OrderSku) order[DB_SKU_INDEX];

			if (!sku.isAllocated()) {
				Object[] row = new Object[ROW_SIZE];
				row[ORDER_NUM_INDEX] = order[DB_ORDER_NUM_INDEX];
				row[ORDER_DATE_INDEX] = order[DB_ORDER_DATE_INDEX];
				row[SKU_CODE_INDEX] = sku.getSkuCode();
				row[PRODUCT_NAME_INDEX] = sku.getDisplayName();
				row[QTY_ORDERED_INDEX] = sku.getQuantity();
				row[RESTOCK_DATE_INDEX] = order[DB_RESTOCK_DATE];
				row[CUST_NUM_INDEX] = order[DB_CUST_UIDPK_INDEX];
				row[CUST_NAME_INDEX] = order[DB_CUST_FIRST_NAME_INDEX] + " " + order[DB_CUST_LAST_NAME_INDEX]; //$NON-NLS-1$
				row[CUST_EMAIL_INDEX] = order[DB_CUST_EMAIL_INDEX];
				returnValues.add(row);
			}
		}
		
		return returnValues;
	}

	/**
	 * This implementation returns a new instance of {@link PreparedStatementBuilderJPAImpl}.
	 * 
	 * @return the preparedStatementBuilder
	 */
	protected StockAllocationPreparedStatementBuilder getPreparedStatementBuilder() {
		return new StockAllocationPreparedStatementBuilderJPAImpl();
	}
}
