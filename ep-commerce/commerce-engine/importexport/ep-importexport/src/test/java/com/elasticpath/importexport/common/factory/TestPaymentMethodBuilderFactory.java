/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.factory;

import com.elasticpath.domain.customer.impl.PaymentTokenImpl;

/**
 * Creates builders for instances of {@link com.elasticpath.plugin.payment.dto.PaymentMethod} populated with sensible default values to be used for
 * testing.
 */
public class TestPaymentMethodBuilderFactory {
	private static final String TEST_TOKEN_DISPLAY_VALUE = "testTokenDisplayValue";

	/**
	 * Creates a {@link PaymentTokenImpl.TokenBuilder} initialized with value set to identity, and a default display value.
	 * @param identity the identity
	 * @return the builder
	 */
	public PaymentTokenImpl.TokenBuilder createPaymentTokenBuilderWithIdentity(final String identity) {
		return new PaymentTokenImpl.TokenBuilder()
				.withDisplayValue(TEST_TOKEN_DISPLAY_VALUE)
				.withValue(identity);
	}

}