/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.transformer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilderFactory;

/**
 * Tests the {@link OrderPaymentMethodTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderPaymentMethodTransformerTest {
	private static final String TEST_ORDER_ID = "testOrderId";
	private static final String TEST_ENCODED_ORDER_ID = Base32Util.encode(TEST_ORDER_ID);
	private static final String TEST_SCOPE = "testScope";
	private static final String TEST_SELF_URI = "/testSelfUri";
	public static final String TEST_TYPE = "testType";

	@Mock
	private OrderPaymentMethodUriBuilderFactory orderPaymentMethodUriBuilderFactory;
	@Mock
	private OrderPaymentMethodUriBuilder orderPaymentMethodUriBuilder;

	@InjectMocks
	private OrderPaymentMethodTransformer orderPaymentMethodTransformer;
	private PaymentMethodEntity paymentMethodEntity;
	private ResourceState<OrderEntity> orderRepresentation;

	@Before
	public void setUpCommonComponentsAndHappyCollaborators() {
		paymentMethodEntity = ResourceTypeFactory.createResourceEntity(PaymentMethodEntity.class);
		orderRepresentation = ResourceState.Builder
				.create(OrderEntity.builder().withOrderId(TEST_ORDER_ID).build())
				.withScope(TEST_SCOPE)
				.build();

		shouldBuildOrderPaymentMethodUri();
	}

	@Test
	public void ensureSelfUriIsBuilt() {
		orderPaymentMethodTransformer.transformToRepresentation(orderRepresentation, paymentMethodEntity);

		verify(orderPaymentMethodUriBuilder, times(1)).build();
	}

	@Test
	public void ensureOrderPaymentMethodCanBeTransformedCorrectly() {
		ResourceState<PaymentMethodEntity> representationResult = orderPaymentMethodTransformer.transformToRepresentation(orderRepresentation,
				paymentMethodEntity);

		assertResourceState(representationResult)
				.self(SelfFactory.createSelf(TEST_SELF_URI))
				.linkCount(0);
	}

	private void shouldBuildOrderPaymentMethodUri() {
		when(orderPaymentMethodUriBuilderFactory.get()).thenReturn(orderPaymentMethodUriBuilder);
		when(orderPaymentMethodUriBuilder.setOrderId(TEST_ENCODED_ORDER_ID)).thenReturn(orderPaymentMethodUriBuilder);
		when(orderPaymentMethodUriBuilder.setScope(TEST_SCOPE)).thenReturn(orderPaymentMethodUriBuilder);
		when(orderPaymentMethodUriBuilder.build()).thenReturn(TEST_SELF_URI);
	}
}
