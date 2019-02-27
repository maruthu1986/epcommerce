/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.handlers.registry.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import com.elasticpath.rest.resource.paymenttokens.handlers.CreatePaymentTokenHandler;
import com.elasticpath.rest.resource.paymenttokens.handlers.registry.CreatePaymentTokenHandlerRegistry;

/**
 * Tests the {@link CreatePaymentTokenHandlerRegistryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("rawtypes")
public class CreatePaymentTokenHandlerRegistryImplTest {
	private static final String TEST_REPRESENTATION_TYPE = "testRepresentationType";
	@Mock
	private CreatePaymentTokenHandler createPaymentTokenHandler;
	private CreatePaymentTokenHandlerRegistry createPaymentTokenHandlerRegistry;

	@Before
	public void setUpCommonExpectationsForTest() {
		when(createPaymentTokenHandler.handledOwnerRepresentationType()).thenReturn(TEST_REPRESENTATION_TYPE);
	}

	@Test
	public void verifyLookupHandlerCallsGetHandledOwnerRepresentationType() {
		new CreatePaymentTokenHandlerRegistryImpl(Arrays.asList(createPaymentTokenHandler)).lookupHandler(TEST_REPRESENTATION_TYPE);

		verify(createPaymentTokenHandler, times(1)).handledOwnerRepresentationType();
	}

	@Test
	public void ensureSuccessfulLookupOfExistingHandler() {
		createPaymentTokenHandlerRegistry = new CreatePaymentTokenHandlerRegistryImpl(Arrays.asList(createPaymentTokenHandler));

		CreatePaymentTokenHandler foundHandler = createPaymentTokenHandlerRegistry.lookupHandler(TEST_REPRESENTATION_TYPE);

		assertEquals("The create payment token handler should be the same as expected", createPaymentTokenHandler, foundHandler);
	}

	@Test(expected = IllegalStateException.class)
	public void ensureAssertionErrorThrownWhenHandlerNotConfiguredForOwningRepresentation() {
		final ArrayList<CreatePaymentTokenHandler> paymentMethodHandlers = new ArrayList<>();

		createPaymentTokenHandlerRegistry = new CreatePaymentTokenHandlerRegistryImpl(paymentMethodHandlers);
		createPaymentTokenHandlerRegistry.lookupHandler(TEST_REPRESENTATION_TYPE);
	}

	@Test
	public void verifyGetHandledOwnerRepresentationTypesCallsGetHandledType() {
		new CreatePaymentTokenHandlerRegistryImpl(Arrays.asList(createPaymentTokenHandler)).getHandledOwnerRepresentationTypes();

		verify(createPaymentTokenHandler, times(1)).handledOwnerRepresentationType();
	}

	@Test
	public void ensureGetHandledOwnerRepresentationTypesReturnsCorrectSetOfTypesForHandlers() {
		createPaymentTokenHandlerRegistry = new CreatePaymentTokenHandlerRegistryImpl(Arrays.asList(createPaymentTokenHandler));

		Set<String> handledTypes = createPaymentTokenHandlerRegistry.getHandledOwnerRepresentationTypes();

		HashSet<String> expectedTypes = new HashSet<>(Arrays.asList(TEST_REPRESENTATION_TYPE));
		assertEquals("The types returned should be the same as the expected set of types", expectedTypes, handledTypes);
	}
}
