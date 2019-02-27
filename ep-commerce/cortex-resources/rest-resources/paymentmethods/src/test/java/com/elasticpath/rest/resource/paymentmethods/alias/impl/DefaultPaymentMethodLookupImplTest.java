/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.resource.commons.handler.registry.PaymentHandlerRegistry;
import com.elasticpath.rest.resource.paymentmethods.alias.PaymentMethodsResourceLinkFactory;
import com.elasticpath.rest.resource.paymentmethods.integration.alias.DefaultPaymentMethodLookupStrategy;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Basic smoke test of {@link DefaultPaymentMethodLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class DefaultPaymentMethodLookupImplTest {
	private static final String SCOPE = "mobee";
	private static final String USER_ID = "userid";
	private static final String PAYMENT_METHOD_ID = "af9f033e-f042-43b8-a22f-07bc5c6caf13";
	private static final String NOT_FOUND_ERROR_MESSAGE = "not found error message";
	private static final String PAYMENT_METHODS = "paymentmethods";
	private static final String DEFAULT = "default";
	private static final String TEST_REPRESENTATION_TYPE = "testRepresentationType";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private DefaultPaymentMethodLookupStrategy defaultPaymentMethodLookupStrategy;
	@Mock
	private PaymentMethodsResourceLinkFactory paymentMethodsResourceLinkFactory;
	@Mock
	private PaymentHandlerRegistry paymentMethodHandlerRegistry;
	@Mock
	private PaymentHandler paymentMethodHandler;
	@InjectMocks
	private DefaultPaymentMethodLookupImpl defaultPaymentMethodIdLookup;

	final PaymentMethodEntity paymentMethod = ResourceTypeFactory.createResourceEntity(PaymentMethodEntity.class);

	@Test
	public void ensureDefaultPaymentIdReturnedIfExists() {
		shouldReturnDefaultPaymentMethodId();

		ExecutionResult<String> defaultPaymentIdReadResult = defaultPaymentMethodIdLookup.getDefaultPaymentMethodId(SCOPE, USER_ID);
		assertExecutionResult(defaultPaymentIdReadResult)
				.isSuccessful()
				.data(PAYMENT_METHOD_ID);
	}

	@Test
	public void ensureNonExistentDefaultPaymentMethodReturnsNotFound() {
		shouldReturnNotFoundForDefaultPaymentMethodId();

		ExecutionResult<String> result = defaultPaymentMethodIdLookup.getDefaultPaymentMethodId(SCOPE, USER_ID);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.NOT_FOUND)
				.isFailure();
	}

	@Test
	public void ensureLinkReturnedIfDefaultPaymentMethodExists() {
		shouldReturnDefaultPaymentMethod();
		shouldFindHandlerForPaymentMethodType();
		ResourceLink expectedLink = createExpectedDefaultPaymentMethodLink();
		shouldReturnPaymentMethodElementLink(expectedLink);

		ExecutionResult<ResourceLink> defaultPaymentLinkReadResult = defaultPaymentMethodIdLookup.getDefaultPaymentMethodElementLink(SCOPE, USER_ID);
		assertExecutionResult(defaultPaymentLinkReadResult)
				.isSuccessful()
				.data(expectedLink);
	}

	@Test
	public void ensureNotFoundReturnedIfDefaultPaymentMethodDoesNotExist() {
		shouldNotFindDefaultPaymentMethod();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		defaultPaymentMethodIdLookup.getDefaultPaymentMethodElementLink(SCOPE, USER_ID);

	}

	@Test(expected = IllegalStateException.class)
	public void ensureServerErrorReturnedIfPaymentMethodHandlerNotFound() {
		shouldReturnDefaultPaymentMethod();
		shouldThrowExceptionFromHandlerRegistry();
		defaultPaymentMethodIdLookup.getDefaultPaymentMethodElementLink(SCOPE, USER_ID);
	}

	private void shouldReturnDefaultPaymentMethodId() {
		when(defaultPaymentMethodLookupStrategy.getDefaultPaymentMethodId(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(PAYMENT_METHOD_ID));
	}

	private void shouldThrowExceptionFromHandlerRegistry() {
		when(paymentMethodHandlerRegistry.lookupHandler(paymentMethod)).thenThrow(new IllegalStateException());
	}

	private void shouldReturnNotFoundForDefaultPaymentMethodId() {
		when(defaultPaymentMethodLookupStrategy.getDefaultPaymentMethodId(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<String>createNotFound(NOT_FOUND_ERROR_MESSAGE));
	}

	private void shouldReturnPaymentMethodElementLink(final ResourceLink expectedLink) {
		when(paymentMethodsResourceLinkFactory.createDefaultPaymentMethodElementLink(SCOPE, TEST_REPRESENTATION_TYPE)).thenReturn(expectedLink);
	}

	private ResourceLink createExpectedDefaultPaymentMethodLink() {
		String defaultPaymentMethodUri = URIUtil.format(PAYMENT_METHODS, SCOPE, DEFAULT);
		return ElementListFactory.createElementOfList(defaultPaymentMethodUri, TEST_REPRESENTATION_TYPE);
	}

	private void shouldFindHandlerForPaymentMethodType() {
		when(paymentMethodHandlerRegistry.lookupHandler(paymentMethod)).thenReturn(paymentMethodHandler);
		when(paymentMethodHandler.representationType()).thenReturn(TEST_REPRESENTATION_TYPE);
	}

	private void shouldReturnDefaultPaymentMethod() {
		when(defaultPaymentMethodLookupStrategy.getDefaultPaymentMethod(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(paymentMethod));
	}

	private void shouldNotFindDefaultPaymentMethod() {
		when(defaultPaymentMethodLookupStrategy.getDefaultPaymentMethod(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<PaymentMethodEntity>createNotFound());
	}
}
