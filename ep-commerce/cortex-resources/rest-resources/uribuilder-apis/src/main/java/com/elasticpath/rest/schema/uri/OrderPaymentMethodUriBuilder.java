/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds the uri to the pamyent method for an order.
 */
public interface OrderPaymentMethodUriBuilder extends ScopedUriBuilder<OrderPaymentMethodUriBuilder> {
	/**
	 * Sets the order id.
	 *
	 * @param orderId the order id
	 * @return this builder
	 */
	OrderPaymentMethodUriBuilder setOrderId(String orderId);
}
