/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.handlers.order.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.PhysicalOrderShipment;

/**
 * An OrderShipmentHandler that handles {@link PhysicalOrderShipment}s.
 */
public class PhysicalOrderShipmentHandler extends AbstractOrderShipmentHandler {

	@Override
	protected boolean canHandle(final OrderShipment orderShipment) {
		return orderShipment instanceof PhysicalOrderShipment;
	}

	@Override
	protected void handleOrderReturnInternal(final OrderReturn orderReturn, final OrderShipment orderShipment) {
		orderReturn.setOrderReturnAddress(((PhysicalOrderShipment) orderShipment).getShipmentAddress());
	}
	
	@Override
	public BigDecimal calculateShippingCost(final OrderShipment orderShipment) {
		return ((PhysicalOrderShipment) orderShipment).getShippingCost();
	}
}
