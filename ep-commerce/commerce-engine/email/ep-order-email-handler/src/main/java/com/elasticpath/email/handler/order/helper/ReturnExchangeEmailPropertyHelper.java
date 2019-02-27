/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.helper;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.email.domain.EmailProperties;

/**
 * Helper for constructing {@link OrderReturn} email properties.
 */
public interface ReturnExchangeEmailPropertyHelper {

	/**
	 * Get email properties for order return email.
	 * 
	 * @param orderReturn - the order return
	 * @return the email properties for order return email
	 */
	EmailProperties getOrderReturnEmailProperties(OrderReturn orderReturn);

}
