/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Date;

import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.order.Order;

/**
 * Represents logging information about changes to <code>Inventory</code>.
 */
public class InventoryAuditImpl implements InventoryAudit {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private int quantity;

	private String comment;

	private String eventOriginator;

	private Order order;

	private InventoryEventType eventType;

	private Date logDate;

	private String reason;

	/**
	 * @return the eventOriginator
	 */
	@Override
	public String getEventOriginator() {
		return eventOriginator;
	}

	/**
	 * @param eventOriginator the eventOriginator to set
	 */
	@Override
	public void setEventOriginator(final String eventOriginator) {
		this.eventOriginator = eventOriginator;
	}

	/**
	 * @return the comment
	 */
	@Override
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	@Override
	public void setComment(final String comment) {
		this.comment = comment;
	}

	/**
	 * @return the adjust quantity on hand reason
	 */
	@Override
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the adjust quantity on hand reason to set
	 */
	@Override
	public void setReason(final String reason) {
		this.reason = reason;
	}

	/**
	 * @return the quantity
	 */
	@Override
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	@Override
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the logDate
	 */
	@Override
	public Date getLogDate() {
		return logDate;
	}

	/**
	 * @param logDate the logDate to set
	 */
	@Override
	public void setLogDate(final Date logDate) {
		this.logDate = logDate;
	}

	/**
	 * Get the event type. For legacy data, return UNKNOWN.
	 * 
	 * @return the event type
	 */
	@Override
	public InventoryEventType getEventType() {
		return eventType;
	}

	/**
	 * Set the event type.
	 * 
	 * @param eventType the eventType to set
	 */
	@Override
	public void setEventType(final InventoryEventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * Get the orderUid if the inventory event is related to an order.
	 * 
	 * @return the orderUid
	 */
	@Override
	public Order getOrder() {
		return order;
	}

	/**
	 * Set the order which the inventory event related to.
	 * 
	 * @param order the order to set
	 */
	@Override
	public void setOrder(final Order order) {
		this.order = order;
	}

}