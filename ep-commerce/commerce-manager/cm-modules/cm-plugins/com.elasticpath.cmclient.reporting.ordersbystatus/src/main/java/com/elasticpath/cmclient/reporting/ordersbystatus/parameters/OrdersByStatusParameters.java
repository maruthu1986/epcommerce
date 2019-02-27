/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersbystatus.parameters;

import java.util.Currency;
import java.util.Date;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.reporting.common.ReportParameters;
import com.elasticpath.cmclient.reporting.ordersbystatus.OrdersByStatusReportMessages;
import com.elasticpath.domain.order.OrderStatus;

/**
 * Represents the parameters defined for the orders by status report.
 */
public final class OrdersByStatusParameters implements ReportParameters {

	/**
	 * Parameter for storeName.
	 */
	public static final String STORE = "store"; //$NON-NLS-1$
	
	/**
	 * Parameter for start date.
	 */
	public static final String START_DATE = "startDate"; //$NON-NLS-1$
	
	/**
	 * Parameter for end date.
	 */
	public static final String END_DATE = "endDate"; //$NON-NLS-1$

	/**
	 * Identifier for the currencyType key.
	 */
	public static final String CURRENCY = "currency"; //$NON-NLS-1$
	
	/**
	 * Identifier for the show exchange only key.
	 */
	public static final String EXCHANGE_ONLY = "isShowExchangeOnly"; //$NON-NLS-1$
	
	/**
	 * Identifier for the internationalized checked status key.
	 */
	public static final String INTERNATIONAL_CHECK_STATUS = "checkedStatuses"; //$NON-NLS-1$
	
	private String storeName;

	private String title;

	private Date startDate;

	private Date endDate;
	
	private List<OrderStatus> checkedOrderStatuses;
	
	private boolean showExchangeOnly;
	
	private Currency currencyType;
	
	/**
	 * Generic Constructor.
	 */
	private OrdersByStatusParameters() {
		// This constructor should not instantiate anything
	}
	/**
	 *Gets the session instance.
	 *
	 * @return the session instance.
	 */
	public static OrdersByStatusParameters getInstance() {
		return CmSingletonUtil.getSessionInstance(OrdersByStatusParameters.class);
	}
	
	/**
	 * Gets the storeName name.
	 * 
	 * @return String the storeName name
	 */
	public String getStore() {
		return storeName;
	}

	/**
	 * Sets the storeName name.
	 * 
	 * @param store the storeName name
	 */
	public void setStore(final String store) {
		this.storeName = store;
	}

	/**
	 * Gets the starting date.
	 * 
	 * @return Date the starting date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the starting date.
	 * 
	 * @param startDate the starting date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return Date the end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the title.
	 * @return the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 * @param title the title to set.
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Gets List of Order Statuses.
	 * @return the checkedOrderStatuses.
	 */
	public List<OrderStatus> getCheckedOrderStatuses() {
		return checkedOrderStatuses;
	}

	/**
	 * Sets List of Order Statuses.
	 * @param checkedOrderStatuses the checkedOrderStatuses to set.
	 */
	public void setCheckedOrderStatuses(final List<OrderStatus> checkedOrderStatuses) {
		this.checkedOrderStatuses = checkedOrderStatuses;
	}
	
	@Override
	public Currency getCurrency() {
		return currencyType;
	}

	@Override
	public void setCurrency(final Currency currency) {
		this.currencyType = currency;
	}
	
	/**
	 * Gets boolean state is exchange order only.
	 * 
	 * @return true if registration is anonymous, false otherwise
	 */
	public boolean isShowExchangeOnly() {
		return showExchangeOnly;
	}

	/**
	 * Sets the boolean state of exchange only.
	 * 
	 * @param showExchangeOnly boolean
	 */
	public void setShowExchangeOnly(final boolean showExchangeOnly) {
		this.showExchangeOnly = showExchangeOnly;
	}
	
	/**
	 * Gets the exchange string for report title.
	 * @return the exchange string for report title
	 */
	public String getExchangeString() {
		if (showExchangeOnly) {
			return OrdersByStatusReportMessages.get().exchangeOrderOnly;
		}
		return OrdersByStatusReportMessages.get().emptyString;
	}
}
