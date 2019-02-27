/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.transform;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.PaymentMethodIdentifierResolver;

/**
 * The test of {@link PaymentMethodTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodTransformerTest {

	private static final String EXPECTED_IDENTIFIER = "expectedIdentifier";
	private static final String TEST_DISPLAY_VALUE = "testDisplayValue";
	private static final String TEST_VALUE = "testValue";

	@Mock
	private PaymentMethodIdentifierResolver paymentMethodIdentifierResolver;
	@InjectMocks
	private PaymentMethodTransformer transformer;

	@Test(expected = UnsupportedOperationException.class)
	public void ensureTransformToDomainIsUnsupported() {
		transformer.transformToDomain(null);
	}

	@Test
	public void ensureCorrectTransformOfPaymentTokenToEntity() {
		final PaymentToken paymentToken = new PaymentTokenImpl.TokenBuilder()
					.withDisplayValue(TEST_DISPLAY_VALUE)
					.withValue(TEST_VALUE)
					.build();

		when(paymentMethodIdentifierResolver.getIdentifierForPaymentMethod(paymentToken)).thenReturn(EXPECTED_IDENTIFIER);
		PaymentMethodEntity paymentMethodEntity = transformer.transformToEntity(paymentToken);

		PaymentTokenEntity paymentTokenEntity = ResourceTypeFactory.adaptResourceEntity(paymentMethodEntity, PaymentTokenEntity.class);

		assertEquals("The payment token display value should be the same as expected", TEST_DISPLAY_VALUE, paymentTokenEntity.getDisplayName());
		assertEquals("The payment token value should be the same as expected", TEST_VALUE, paymentTokenEntity.getToken());
		assertEquals("The payment token id should be the same as expected", EXPECTED_IDENTIFIER,
				paymentTokenEntity.getPaymentMethodId());
	}
}
