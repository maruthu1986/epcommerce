/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds a URI to a single payment method.
 */
public interface PaymentMethodUriBuilder extends ScopedUriBuilder<PaymentMethodUriBuilder> {

	/**
	 * Set the payment method ID.
	 *
	 * @param paymentMethodId the payment method ID
	 * @return this builder
	 */
	PaymentMethodUriBuilder setPaymentMethodId(String paymentMethodId);
}
