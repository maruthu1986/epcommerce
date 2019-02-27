/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds the URI of an order's delivery.
 */
public interface DeliveryUriBuilder extends ScopedUriBuilder<DeliveryUriBuilder> {

	/**
	 * Sets the order id.
	 *
	 * @param orderId the order id
	 * @return the delivery uri builder
	 */
	DeliveryUriBuilder setOrderId(String orderId);

	/**
	 * Sets the delivery id.
	 *
	 * @param deliveryId the delivery id
	 * @return the delivery uri builder
	 */
	DeliveryUriBuilder setDeliveryId(String deliveryId);
}
