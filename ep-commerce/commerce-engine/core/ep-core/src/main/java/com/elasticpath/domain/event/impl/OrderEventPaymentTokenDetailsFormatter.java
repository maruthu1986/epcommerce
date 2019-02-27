/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.event.impl;

import com.elasticpath.domain.event.OrderEventPaymentDetailFormatter;
import com.elasticpath.domain.order.OrderPayment;

/**
 * Formats Payment Token Details for {@link com.elasticpath.domain.order.OrderEvent}. 
 */
public class OrderEventPaymentTokenDetailsFormatter implements OrderEventPaymentDetailFormatter {

	private static final char SPACE_CHAR = ' ';

	@Override
	public String formatPaymentDetails(final OrderPayment orderPayment) {
		StringBuilder paymentTokenDetails = new StringBuilder();
		paymentTokenDetails.append(SPACE_CHAR);
		paymentTokenDetails.append(orderPayment.getDisplayValue());
		return paymentTokenDetails.toString();
	}

}
