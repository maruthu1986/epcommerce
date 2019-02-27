/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.handlers.order.impl;

import java.text.MessageFormat;

import com.elasticpath.commons.handlers.order.OrderShipmentHandler;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Abstract class from which all Order Shipment Handlers extends from. 
 *
 */
public abstract class AbstractOrderShipmentHandler implements OrderShipmentHandler {

	private ProductSkuLookup productSkuLookup;

	@Override
	public final void handleOrderReturn(final OrderReturn orderReturn, final OrderShipment orderShipment) {

		if (!canHandle(orderShipment)) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Unable to handle order shipment of type {0}", orderShipment.getClass()));
		}
		orderReturn.populateOrderReturn(orderShipment.getOrder(), orderShipment, orderReturn.getReturnType());
		orderReturn.updateOrderReturnableQuantity(orderShipment.getOrder(), getProductSkuLookup());
		handleOrderReturnInternal(orderReturn, orderShipment);
	}
	
	/**
	 * Handles any additional mapping for the order return based on the type of order shipment.
	 * @param orderReturn The OrderReturn object to populate fields for
	 * @param orderShipment The OrderShipment instance to populate fields from
	 */
	protected abstract void handleOrderReturnInternal(OrderReturn orderReturn, OrderShipment orderShipment);

	/**
	 * Determines whether or not the handler can handle the orderShipment.
	 * @param orderShipment The orderShipment to handle
	 * @return true if this handler can handle the instance of orderShipment, false otherwise
	 */
	protected abstract boolean canHandle(OrderShipment orderShipment);

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}
}
 
