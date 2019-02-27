/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.factory;

import com.elasticpath.common.dto.customer.builder.PaymentTokenDTOBuilder;

/**
 * A factory for producing payment method dto builders.
 */
public class TestPaymentDTOBuilderFactory {
	private static final String TEST_PAYMENT_TOKEN_DISPLAY_VALUE = "testPaymentTokenDisplayValue";

	/**
	 * Create {@link PaymentTokenDTOBuilder} pre-populated with default test values.
	 *
	 * @param value the value to use
	 *
	 * @return the {@link PaymentTokenDTOBuilder}
	 */
	public PaymentTokenDTOBuilder createPaymentTokenWithValue(final String value) {
		return new PaymentTokenDTOBuilder()
				.withPaymentTokenDisplayValue(TEST_PAYMENT_TOKEN_DISPLAY_VALUE)
				.withPaymentTokenValue(value);
	}
}
