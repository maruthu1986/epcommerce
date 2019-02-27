/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.PaymentMethodIdentifierResolver;


/**
 * Unit Test for {@link PaymentTokenTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentTokenTransformerTest {
	private static final String VALUE = "value";
	private static final String DISPLAY = "display";
	public static final String TEST_CORRELATION_ID = "testCorrelationId";

	@Mock
	private PaymentMethodIdentifierResolver paymentMethodIdentifierResolver;
	@InjectMocks
	private PaymentTokenTransformer paymentTokenTransformer;

	private PaymentTokenEntity paymentTokenEntity;
	private PaymentToken paymentToken;

	@Before
	public void setUpTestComponents() {
		shouldIdentifyPaymentMethod();

		paymentTokenEntity = PaymentTokenEntity.builder()
				.withDisplayName(DISPLAY)
				.withToken(VALUE)
				.withPaymentMethodId(TEST_CORRELATION_ID)
				.build();

		paymentToken = new PaymentTokenImpl.TokenBuilder()
				.withDisplayValue(DISPLAY)
				.withValue(VALUE)
				.build();
	}

	@Test
	public void ensurePaymentTokenIdentifierIsResolved() {
		paymentTokenTransformer.transformToEntity(paymentToken);

		verify(paymentMethodIdentifierResolver, times(1)).getIdentifierForPaymentMethod(paymentToken);
	}

	@Test
	public void ensureSuccessfulTransformDtoToDomain() {
		assertEquals("The returned payment token should be the same as expected", paymentToken,
				paymentTokenTransformer.transformToDomain(paymentTokenEntity, null));
	}

	@Test
	public void ensureSuccessfulTransformToEntity() {
		assertEquals("The returned payment token entity should be the same as expected", paymentTokenEntity,
				paymentTokenTransformer.transformToEntity(paymentToken, null));
	}

	@Test(expected = NullPointerException.class)
	public void ensureNPEIsThrownWhenNullPaymentTokenIsTransformedToEntity() {
		paymentTokenTransformer.transformToEntity(null);
	}

	@Test(expected = NullPointerException.class)
	public void ensureNPEIsThrownWhenNullPaymentTokenEntityIsTransformedToDomain() {
		paymentTokenTransformer.transformToDomain(null);
	}

	private void shouldIdentifyPaymentMethod() {
		when(paymentMethodIdentifierResolver.getIdentifierForPaymentMethod(any(PaymentMethod.class)))
				.thenReturn(TEST_CORRELATION_ID);
	}
}