/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.handlers.order;

import java.math.BigDecimal;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;

/**
 * An interface to handle behaviour differences between the different types of Order Shipments.
 */
public interface OrderShipmentHandler {

	/**
	 * Handles an OrderReturn based on the OrderShipment. Will set the address
	 * information of the OrderReturn based on the order shipment.
	 * 
	 * @param orderReturn The OrderReturn to populate data for.
	 * @param orderShipment The OrderShipment to base information for.
	 */
	void handleOrderReturn(OrderReturn orderReturn, OrderShipment orderShipment);
	
	/**
	 * Retrieves the shipping cost for this order shipment.
	 * @param orderShipment The order shipment to get the cost for
	 * @return The shipping cost for the shipment.
	 */
	BigDecimal calculateShippingCost(OrderShipment orderShipment);
}
