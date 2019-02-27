/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.order;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;

/**
 * Order return validator used to verify an order return before it is created.
 */
public interface OrderReturnValidator {
	
	/**
	 * Validates an order return.
	 * 
	 * @param orderReturn order return
	 * @param orderShipment order shipment
	 * @throws OrderReturnInvalidException on validation error
	 */
	void validate(OrderReturn orderReturn, OrderShipment orderShipment) throws OrderReturnInvalidException;
}
