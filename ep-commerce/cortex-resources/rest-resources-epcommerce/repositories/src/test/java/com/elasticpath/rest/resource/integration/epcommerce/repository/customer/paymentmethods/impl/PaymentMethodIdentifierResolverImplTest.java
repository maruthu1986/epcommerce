/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.domain.customer.impl.AbstractPaymentMethodImpl;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.PaymentMethodIdentifierResolver;

/**
 * Tests the {@link PaymentMethodIdentifierResolverImpl}.
 */
public class PaymentMethodIdentifierResolverImplTest {
	private static final long TEST_IDENTIFIER = 187L;
	private final PaymentMethodIdentifierResolver paymentMethodIdentifierResolver = new PaymentMethodIdentifierResolverImpl();

	@Test
	public void ensurePaymentMethodIdentifierCanBeResolvedSuccessfully() {
		AbstractPaymentMethodImpl paymentMethod = new AbstractPaymentMethodImpl() {
			@Override
			public AbstractPaymentMethodImpl copy() {
				throw new UnsupportedOperationException("Not required for test.");
			} };
		paymentMethod.setUidPk(TEST_IDENTIFIER);
		String paymentMethodIdentifier = paymentMethodIdentifierResolver.getIdentifierForPaymentMethod(paymentMethod);

		assertEquals("The identified payment method should be the same as expected", String.valueOf(TEST_IDENTIFIER), paymentMethodIdentifier);
	}

	@Test(expected = NullPointerException.class)
	public void ensurePaymentMethodIdentifierThrowsNPEWhenNullIsPassed() {
		paymentMethodIdentifierResolver.getIdentifierForPaymentMethod(null);
	}

}
