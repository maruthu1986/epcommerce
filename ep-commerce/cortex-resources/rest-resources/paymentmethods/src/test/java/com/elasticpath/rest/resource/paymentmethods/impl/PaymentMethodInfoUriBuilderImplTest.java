/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link PaymentMethodInfoUriBuilderImpl}.
 */
public class PaymentMethodInfoUriBuilderImplTest {
	@Test(expected = AssertionError.class)
	public void ensureBuildThrowsExceptionWhenOrderUriIsUnspecified() {
		PaymentMethodInfoUriBuilderImpl builder = new PaymentMethodInfoUriBuilderImpl("");
		builder.build();
	}

	/**
	 * Ensure correct uri construction.
	 */
	@Test
	public void ensureCorrectUriConstruction() {
		String paymentMethodInfoUri = new PaymentMethodInfoUriBuilderImpl("resourceservername")
				.setSourceUri("orders/uri")
				.build();
		String expectedUri = "/resourceservername/info/orders/uri";
		assertEquals("payment method info uri", expectedUri, paymentMethodInfoUri);
	}
}
