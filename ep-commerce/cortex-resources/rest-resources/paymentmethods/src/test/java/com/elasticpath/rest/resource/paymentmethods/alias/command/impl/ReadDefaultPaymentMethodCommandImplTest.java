/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias.command.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationContextFactory;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.paymentmethods.alias.DefaultPaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.alias.command.ReadDefaultPaymentMethodCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link ReadDefaultPaymentMethodCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadDefaultPaymentMethodCommandImplTest {

	private static final String DEFAULT_PAYMENT_ID = "DEFAULT_PAYMENT_ID";
	private static final String SCOPE = "SCOPE";
	private static final String USER_ID = "USER_ID";
	private static final String PAYMENT_METHOD_URI = URIUtil.format("paymethods", SCOPE, DEFAULT_PAYMENT_ID);

	@Mock
	private DefaultPaymentMethodLookup defaultPaymentMethodLookup;
	@Mock
	private PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory;
	@Mock
	private PaymentMethodUriBuilder paymentMethodUriBuilder;

	private final ResourceOperationContext resourceOperationContext = TestResourceOperationContextFactory.create(
			TestResourceOperationFactory.createRead(PAYMENT_METHOD_URI,
					TestSubjectFactory.createWithScopeAndUserId(SCOPE, USER_ID)));

	@Before
	public void setup() {
		when(paymentMethodUriBuilderFactory.get()).thenReturn(paymentMethodUriBuilder);
		when(paymentMethodUriBuilder.setPaymentMethodId(any())).thenReturn(paymentMethodUriBuilder);
		when(paymentMethodUriBuilder.setScope(any())).thenReturn(paymentMethodUriBuilder);
	}

	/**
	 * Test default payment method lookup.
	 */
	@Test
	public void testDefaultPaymentMethodLookup() {
		when(defaultPaymentMethodLookup.getDefaultPaymentMethodId(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(DEFAULT_PAYMENT_ID));
		when(paymentMethodUriBuilder.build()).thenReturn(PAYMENT_METHOD_URI);

		ReadDefaultPaymentMethodCommand command = createReadDefaultPaymentMethodCommand();
		ExecutionResult<ResourceState<ResourceEntity>> result = command.execute();

		assertThat(result.getResourceStatus())
				.as("This should return the expected resource status.")
				.isEqualTo(ResourceStatus.SEE_OTHER);

		assertThat(ResourceStateUtil.getSelfUri(result.getData()))
				.isEqualTo(PAYMENT_METHOD_URI);
	}

	/**
	 * Test default payment method lookup when payment method id not found.
	 */
	@Test(expected = BrokenChainException.class)
	public void testDefaultPaymentMethodLookupWhenPaymentMethodIdNotFound() {
		when(defaultPaymentMethodLookup.getDefaultPaymentMethodId(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<String>createNotFound("payment method id not found"));

		ReadDefaultPaymentMethodCommand command = createReadDefaultPaymentMethodCommand();
		command.execute();
	}

	private ReadDefaultPaymentMethodCommand createReadDefaultPaymentMethodCommand() {
		ReadDefaultPaymentMethodCommandImpl readDefaultPaymentMethodCommand =
				new ReadDefaultPaymentMethodCommandImpl(defaultPaymentMethodLookup, paymentMethodUriBuilderFactory,
						resourceOperationContext);

		ReadDefaultPaymentMethodCommandImpl.BuilderImpl builder =
				new ReadDefaultPaymentMethodCommandImpl.BuilderImpl(readDefaultPaymentMethodCommand);

		return builder
				.setScope(SCOPE)
				.build();
	}
}
