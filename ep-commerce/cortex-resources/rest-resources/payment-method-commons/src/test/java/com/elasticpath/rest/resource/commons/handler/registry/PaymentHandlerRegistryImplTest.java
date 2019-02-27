/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.commons.handler.registry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Tests the {@link PaymentHandlerRegistryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentHandlerRegistryImplTest {

	private static final String TEST_PAYMENT_TYPE = "testPaymentType";
	private static final String TEST_PAYMENT_TYPE_2 = "testPaymentType2";
	@Mock
	private PaymentHandler paymentHandler1;
	@Mock
	private PaymentHandler paymentHandler2;

	@Before
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void setUpCommonExpectationsForTest() {
		Class testPaymentEntityClass = TestPaymentEntity.class;
		when(paymentHandler1.handledType()).thenReturn(testPaymentEntityClass);
		when(paymentHandler1.representationType()).thenReturn(TEST_PAYMENT_TYPE);
		when(paymentHandler2.representationType()).thenReturn(TEST_PAYMENT_TYPE_2);
	}

	@Test
	public void ensureSuccessfulLookupOfExistingHandler() {
		final PaymentHandlerRegistry paymentMethodHandlerRegistry = createPaymentMethodHandlerRegistryWithHandlers();
		PaymentHandler paymentMethodHandler = paymentMethodHandlerRegistry.lookupHandler(
				ResourceTypeFactory.createResourceEntity(TestPaymentEntity.class));

		assertEquals("The payment method handler should be the same as expected", paymentHandler1, paymentMethodHandler);
	}

	@Test(expected = IllegalStateException.class)
	public void ensureAssertionErrorThrownWhenHandlerNotConfiguredForType() {
		final ArrayList<PaymentHandler> paymentMethodHandlers = new ArrayList<>();

		final PaymentHandlerRegistry paymentMethodHandlerRegistry = new PaymentHandlerRegistryImpl(paymentMethodHandlers);
		paymentMethodHandlerRegistry.lookupHandler(
				ResourceTypeFactory.createResourceEntity(TestPaymentEntity.class));
	}

	@Test
	public void ensureGetHandledPaymentRepresentationTypesReturnsCorrectSetOfTypesForHandlers() {
		final PaymentHandlerRegistry paymentMethodHandlerRegistry = createPaymentMethodHandlerRegistryWithHandlers();

		Set<String> handledTypes = paymentMethodHandlerRegistry.getHandledPaymentRepresentationTypes();

		HashSet<String> expectedTypes = new HashSet<>(Arrays.asList(TEST_PAYMENT_TYPE, TEST_PAYMENT_TYPE_2));
		assertEquals("The types returned should be the same as the expected set of types", expectedTypes, handledTypes);
	}

	@Test
	public void ensureGetHandledPaymentMRepresentationTypesReturnsEmptySetForNoHandlersConfigured() {
		PaymentHandlerRegistry paymentMethodHandlerRegistry = new PaymentHandlerRegistryImpl(new ArrayList<PaymentHandler>());
		Set<String> handledTypes = paymentMethodHandlerRegistry.getHandledPaymentRepresentationTypes();

		assertThat("The set returned should be empty given no handlers are configured", handledTypes, empty());
	}

	private PaymentHandlerRegistry createPaymentMethodHandlerRegistryWithHandlers() {
		final ArrayList<PaymentHandler> paymentMethodHandlers = new ArrayList<>();
		paymentMethodHandlers.add(paymentHandler1);
		paymentMethodHandlers.add(paymentHandler2);
		return new PaymentHandlerRegistryImpl(paymentMethodHandlers);
	}

	/**
	 * Test payment entity for payment handler 1.
	 */
	public interface TestPaymentEntity extends ResourceEntity {

	}

	/**
	 * Test payment entity for payment handler 2.
	 */
	public interface TestPaymentEntity2 extends ResourceEntity {

	}
}
