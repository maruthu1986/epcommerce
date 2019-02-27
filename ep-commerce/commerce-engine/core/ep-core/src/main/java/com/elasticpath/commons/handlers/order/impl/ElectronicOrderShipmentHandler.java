/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.handlers.order.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;

/**
 * An OrderShipmentHandler that handles {@link ElectronicOrderShipment}s.
 */
public class ElectronicOrderShipmentHandler extends AbstractOrderShipmentHandler {
	
	@Override
	protected boolean canHandle(final OrderShipment orderShipment) {
		return orderShipment instanceof ElectronicOrderShipment;
	}

	@Override
	protected void handleOrderReturnInternal(final OrderReturn orderReturn, final OrderShipment orderShipment) {
		orderReturn.setOrderReturnAddress(orderShipment.getOrder().getBillingAddress());

	}

	@Override
	public BigDecimal calculateShippingCost(final OrderShipment orderShipment) {
		return BigDecimal.ZERO;
	}
}
