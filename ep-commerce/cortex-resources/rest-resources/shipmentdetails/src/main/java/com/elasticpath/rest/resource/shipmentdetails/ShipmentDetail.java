/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails;

/**
 * Wrapper holding shipment details information, that has been processed for resource usage.
 */
public final class ShipmentDetail {

	private String orderId;
	private String deliveryId;

	/**
	 * Sets the order id.
	 *
	 * @param orderId the new order id
	 * @return the shipment details
	 */
	public ShipmentDetail setOrderId(final String orderId) {
		this.orderId = orderId;
		return this;
	}

	/**
	 * Gets the order id.
	 *
	 * @return the order id
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * Sets the delivery id.
	 *
	 * @param deliveryId the new delivery id
	 * @return the shipment details
	 */
	public ShipmentDetail setDeliveryId(final String deliveryId) {
		this.deliveryId = deliveryId;
		return this;
	}

	/**
	 * Gets the delivery id.
	 *
	 * @return the delivery id
	 */
	public String getDeliveryId() {
		return deliveryId;
	}
}
