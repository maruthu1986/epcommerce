/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.handlers.order;

import com.elasticpath.domain.shipping.ShipmentType;

/**
 * OrderShipment handler factory class for getting instance of {@link OrderShipmentHandler}.
 */
public interface OrderShipmentHandlerFactory {

	/**
	 * Gets the OrderShipmentHandler based on the OrderShipment type passed in.
	 * @param orderShipmentType The OrderShipmentType to look up the handler for
	 * @return An instance of OrderShipmentHandler based on the type of OrderShipment
	 */
	OrderShipmentHandler getOrderShipmentHandler(ShipmentType orderShipmentType);
}