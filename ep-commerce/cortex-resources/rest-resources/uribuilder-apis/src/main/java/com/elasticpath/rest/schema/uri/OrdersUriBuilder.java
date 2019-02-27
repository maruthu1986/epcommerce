/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds URIs for orders.
 */
public interface OrdersUriBuilder extends ScopedUriBuilder<OrdersUriBuilder> {

	/**
	 * Set the order ID.
	 *
	 * @param orderId order ID.
	 * @return the builder
	 */
	OrdersUriBuilder setOrderId(String orderId);
}
