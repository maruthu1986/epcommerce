/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.handlers.order.impl;

import java.text.MessageFormat;
import java.util.Map;

import com.elasticpath.commons.handlers.order.OrderShipmentHandler;
import com.elasticpath.commons.handlers.order.OrderShipmentHandlerFactory;
import com.elasticpath.domain.shipping.ShipmentType;

/**
 * Concrete class for {@link OrderShipmentHandlerFactory}.
 */
public class OrderShipmentHandlerFactoryImpl implements OrderShipmentHandlerFactory {
	
	private Map<ShipmentType, OrderShipmentHandler> orderShipmentHandlerMap;

	@Override
	public OrderShipmentHandler getOrderShipmentHandler(final ShipmentType orderShipmentType) {

		if (!getOrderShipmentHandlerMap().containsKey(orderShipmentType)) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Unable to find handler for shipment type {0}",
					orderShipmentType));
		}
		return getOrderShipmentHandlerMap().get(orderShipmentType);
	}
	
	/**
	 * Sets the OrderShipmentHandlerMap.
	 * @param orderShipmentHandlerMap The OrderShipmentHandler map to set
	 */
	public void setOrderShipmentHandlerMap(final Map<ShipmentType, OrderShipmentHandler> orderShipmentHandlerMap) {
		this.orderShipmentHandlerMap = orderShipmentHandlerMap;
	}

	/**
	 * Gets the OrderShipmentHandler map.
	 * @return The OrderShipmentHandler Map
	 */
	protected Map<ShipmentType, OrderShipmentHandler> getOrderShipmentHandlerMap() {
		return orderShipmentHandlerMap;
	}
}
