/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Tests {@link PaymentMethodTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PaymentMethodTransformerTest {

	private static final String SCOPE = "scope";
	private static final String PAYMENT_METHOD_URI = "paymentMethodUri";
	private static final String PAYMENT_METHOD_LIST_URI = "/paymentMethodListUri";
	private static final String TEST_PAYMENT_METHOD_ID = "/testPaymentMethodId";
	@Mock
	private PaymentMethodListUriBuilderFactory paymentMethodListUriBuilderFactory;
	@Mock
	private PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory;
	@InjectMocks
	private PaymentMethodTransformer paymentMethodTransformer;

	@Before
	public void setUpCommonComponents() {
		when(paymentMethodListUriBuilderFactory.get()).thenReturn(new TestPaymentListUriBuilder());
		when(paymentMethodUriBuilderFactory.get()).thenReturn(new TestPaymentMethodUriBuilder());
	}

	@Test
	public void ensureCorrectTransformationOfPaymentMethodToRepresentation() {
		PaymentMethodEntity paymentMethodEntity = PaymentTokenEntity.builder()
				.withPaymentMethodId(TEST_PAYMENT_METHOD_ID)
				.build();
		ResourceState<PaymentMethodEntity> expectedState = ResourceState.Builder
				.create(paymentMethodEntity)
				.withSelf(SelfFactory.createSelf(PAYMENT_METHOD_URI))
				.addingLinks(ElementListFactory.createListWithoutElement(PAYMENT_METHOD_LIST_URI, CollectionsMediaTypes.LINKS.id()))
				.withScope(SCOPE)
				.build();


		ResourceState<PaymentMethodEntity> paymentMethod = paymentMethodTransformer.transformToRepresentation(SCOPE, paymentMethodEntity);

		assertEquals("The expected resource state should have been built", expectedState, paymentMethod);
	}

	/**
	 * Test payment method uri builder.
	 */
	private class TestPaymentMethodUriBuilder implements PaymentMethodUriBuilder {

		@Override
		public PaymentMethodUriBuilder setPaymentMethodId(final String paymentMethodId) {
			return this;
		}

		@Override
		public PaymentMethodUriBuilder setScope(final String scope) {
			return this;
		}

		@Override
		public String build() {
			return PAYMENT_METHOD_URI;
		}
	}

	/**
	 * Test payment method list uri builder.
	 */
	private class TestPaymentListUriBuilder implements PaymentMethodListUriBuilder {

		@Override
		public PaymentMethodListUriBuilder setScope(final String scope) {
			return this;
		}

		@Override
		public String build() {
			return PAYMENT_METHOD_LIST_URI;
		}
	}
}
