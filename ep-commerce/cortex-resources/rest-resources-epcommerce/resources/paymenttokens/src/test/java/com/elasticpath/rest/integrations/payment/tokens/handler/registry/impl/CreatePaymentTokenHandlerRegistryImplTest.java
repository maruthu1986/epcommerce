/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler.registry.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.integrations.payment.tokens.handler.CreatePaymentTokenHandler;
import com.elasticpath.rest.integrations.payment.tokens.handler.registry.CreatePaymentTokenHandlerRegistry;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;


/**
 * Tests the {@link CreatePaymentTokenHandlerRegistryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreatePaymentTokenHandlerRegistryImplTest {
	@Mock
	private CreatePaymentTokenHandler createPaymentTokenHandler;
	private CreatePaymentTokenHandlerRegistry createPaymentTokenHandlerRegistry;

	@Before
	public void setUpHappyCollaborators() {
		when(createPaymentTokenHandler.getHandledOwnerType()).thenReturn(PaymentTokenOwnerType.ORDER_TYPE);
	}

	@Test
	public void verifyLookupHandlerCallsGetHandledOwnerType() {
		new CreatePaymentTokenHandlerRegistryImpl(Arrays.asList(createPaymentTokenHandler)).lookupHandler(PaymentTokenOwnerType.ORDER_TYPE);

		verify(createPaymentTokenHandler, times(1)).getHandledOwnerType();
	}

	@Test
	public void ensureSuccessfulLookupOfHandler() {
		createPaymentTokenHandlerRegistry = new CreatePaymentTokenHandlerRegistryImpl(Arrays.asList(createPaymentTokenHandler));

		CreatePaymentTokenHandler foundHandler = createPaymentTokenHandlerRegistry.lookupHandler(PaymentTokenOwnerType.ORDER_TYPE);

		assertEquals("The create payment token handler should be the same as expected", createPaymentTokenHandler, foundHandler);
	}

	@Test(expected = IllegalStateException.class)
	public void ensureAssertionErrorThrownWhenHandlerNotConfiguredForOwningRepresentation() {
		final ArrayList<CreatePaymentTokenHandler> paymentMethodHandlers = new ArrayList<>();

		createPaymentTokenHandlerRegistry = new CreatePaymentTokenHandlerRegistryImpl(paymentMethodHandlers);
		createPaymentTokenHandlerRegistry.lookupHandler(PaymentTokenOwnerType.ORDER_TYPE);
	}
}
