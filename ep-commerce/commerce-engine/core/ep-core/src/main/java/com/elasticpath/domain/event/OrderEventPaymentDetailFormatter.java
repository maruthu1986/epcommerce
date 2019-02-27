/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.event;

import com.elasticpath.domain.order.OrderPayment;

/**
 * Gets the payment details from an {@link OrderPayment} for an {@link com.elasticpath.domain.order.OrderPayment}.
 */
public interface OrderEventPaymentDetailFormatter {

	/**
	 * Format payment details.
	 *
	 * @param orderPayment the order payment
	 * @return the string
	 */
	String formatPaymentDetails(OrderPayment orderPayment);
}
