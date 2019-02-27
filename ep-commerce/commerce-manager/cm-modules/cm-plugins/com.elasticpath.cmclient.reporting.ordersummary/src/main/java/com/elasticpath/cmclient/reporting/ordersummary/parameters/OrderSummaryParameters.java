/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.reporting.ordersummary.parameters;

import java.util.Currency;
import java.util.Date;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.reporting.common.ReportParameters;
import com.elasticpath.cmclient.reporting.ordersummary.OrderSummaryReportMessages;
import com.elasticpath.domain.order.OrderStatus;

/**
 * Model for order summary report's parameters.
 */
public final class OrderSummaryParameters implements ReportParameters {
	
	private String store;
	private Date startDate;
	private Date endDate;
	private boolean showExchangeOnly;
	private Currency currency;
	private String title;
	private List<OrderStatus> checkedOrderStatuses;

	/**
	 * Generic Constructor.
	 */
	private OrderSummaryParameters() {
		// This constructor should not instantiate anything
	}
	/**
	 *Gets the session instance.
	 *
	 * @return The session instance .
	 */
	public static OrderSummaryParameters getInstance() {
		return CmSingletonUtil.getSessionInstance(OrderSummaryParameters.class);
	}
	
	
	/**
	 * Gets the store name.
	 * 
	 * @return String the store name
	 */
	public String getStore() {
		return store;
	}

	/**
	 * Sets the store name.
	 * 
	 * @param store the store name
	 */
	public void setStore(final String store) {
		this.store = store;
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

	@Override
	public Currency getCurrency() {
		return currency;
	}

	@Override
	public void setCurrency(final Currency currency) {
		this.currency = currency;
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

	/**
	 * Gets the exchange string for report title.
	 * @return the exchange string for report title
	 */
	public String getExchangeString() {
		if (showExchangeOnly) {
			return OrderSummaryReportMessages.get().exchangeOrderOnly;
		}
		return OrderSummaryReportMessages.get().emptyString;
	}

}
