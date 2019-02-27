/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests the {@link OrderPaymentMethodUriBuilderImpl}.
 */
public class OrderPaymentMethodUriBuilderImplTest {

	public static final String RESOURCE_SERVER_NAME = "paymentmethods";
	private static final String TEST_ORDER_ID = "testOrderId";
	private static final String TEST_SCOPE = "testScope";
	private static final String TEST_ORDERS_URI = "/orders/testscope/testorderid";


	private OrderPaymentMethodUriBuilder orderPaymentMethodUriBuilder;

	@Before
	public void setupTestComponentsAndHappyCollaborators() {
		orderPaymentMethodUriBuilder = new OrderPaymentMethodUriBuilderImpl(RESOURCE_SERVER_NAME);
	}

	@Test
	public void ensureOrderPaymentMethodUriIsBuiltCorrectly() {
		String builtUri = buildOrderPaymentMethodUri();
		assertThat(builtUri)
				.as("The order payment method uri should be the same as expected")
				.isEqualTo(URIUtil.format(RESOURCE_SERVER_NAME, TEST_ORDERS_URI));
	}

	private String buildOrderPaymentMethodUri() {
		return orderPaymentMethodUriBuilder
				.setOrderId(TEST_ORDER_ID)
				.setScope(TEST_SCOPE)
				.build();
	}
}
