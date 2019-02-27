/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges.parameters;

import java.util.Currency;
import java.util.Date;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.reporting.common.ReportParameters;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;

/**
 * Represents the parameters defined for the returns and exchanges report.
 */
public final class ReturnsAndExchangesParameters implements ReportParameters {

	//Fields
	private Date startDate;
	private Date endDate;
	private Currency currency;
	private String store;
	private OrderReturnType rmaType;
	private List<OrderReturnStatus> checkedOrderReturnStatuses;
	private String title;

	/**
	 * Generic Constructor.
	 */
	private ReturnsAndExchangesParameters() {
		// This constructor should not instantiate anything
	}
	/**
	 * Gets the Session instance.
	 *
	 * @return the session instance.
	 */
	public static ReturnsAndExchangesParameters getInstance() {
		return CmSingletonUtil.getSessionInstance(ReturnsAndExchangesParameters.class);
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
	 * Gets the return type.
	 * 
	 * @return the rmaType
	 */
	public OrderReturnType getRmaType() {
		return rmaType;
	}

	/**
	 * Sets the return type.
	 * 
	 * @param rmaType the rmaType to set
	 */
	public void setRmaType(final OrderReturnType rmaType) {
		this.rmaType = rmaType;
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
	 * @return the checkedOrderReturnStatuses.
	 */
	public List<OrderReturnStatus> getCheckedOrderReturnStatuses() {
		return checkedOrderReturnStatuses;
	}

	/**
	 * Sets List of Order Return Statuses.
	 * @param checkedOrderReturnStatuses the checkedOrderReturnStatuses to set.
	 */
	public void setCheckedOrderStatuses(final List<OrderReturnStatus> checkedOrderReturnStatuses) {
		this.checkedOrderReturnStatuses = checkedOrderReturnStatuses;
	}

	/**
	 * Sets the store code.
	 *
	 * @param store the store code
	 */
	public void setStore(final String store) {
		this.store = store;
	}
	
	/**
	 * Gets the store code.
	 *
	 * @return store the store code
	 */
	public String getStore() {
		return store;
	}
	
	/**
	 * Gets the currency.
	 * @return currency name as String
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Sets the currency name.
	 * @param currency the name of the currency
	 */
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}
}
