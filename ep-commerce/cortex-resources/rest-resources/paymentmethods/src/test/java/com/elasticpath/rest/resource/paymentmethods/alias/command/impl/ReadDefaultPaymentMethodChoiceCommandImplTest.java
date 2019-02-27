/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias.command.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.alias.DefaultPaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.alias.command.ReadDefaultPaymentMethodChoiceCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link ReadDefaultPaymentMethodChoiceCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadDefaultPaymentMethodChoiceCommandImplTest {

	private static final String DEFAULT_PAYMENT_ID = "DEFAULT_PAYMENT_ID";
	private static final String SCOPE = "SCOPE";
	private static final String USER_ID = "USER_ID";
	private static final String ORDER_URI = URIUtil.format("orders", SCOPE, "orderid");
	private static final String PAYMENT_METHOD_URI = URIUtil.format("paymethods", SCOPE, DEFAULT_PAYMENT_ID);

	@Mock
	private DefaultPaymentMethodLookup defaultPaymentMethodLookup;

	@Mock
	private PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory;
	@Mock
	private PaymentMethodUriBuilder paymentMethodUriBuilder;

	private final ResourceOperationContext resourceOperationContext = TestResourceOperationContextFactory.create(
			TestResourceOperationFactory.createRead(ORDER_URI,
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

		ReadDefaultPaymentMethodChoiceCommand command = createReadDefaultPaymentMethodCommand();
		ExecutionResult<ResourceState<ResourceEntity>> result = command.execute();

		assertThat(result.getResourceStatus())
				.as("This should return the expected resource status.")
				.isEqualTo(ResourceStatus.SEE_OTHER);

		assertThat(ResourceStateUtil.getSelfUri(result.getData()))
				.as("This should return the expected self URI.")
				.isEqualTo(URIUtil.format(PAYMENT_METHOD_URI, Selector.URI_PART, ORDER_URI));

		verify(defaultPaymentMethodLookup, times(1)).getDefaultPaymentMethodId(SCOPE, USER_ID);

	}

	/**
	 * Test default payment method lookup when payment method id not found.
	 */
	@Test(expected = BrokenChainException.class)
	public void testDefaultPaymentMethodLookupWhenPaymentMethodIdNotFound() {
		when(defaultPaymentMethodLookup.getDefaultPaymentMethodId(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<String>createNotFound("payment method id not found"));

		ReadDefaultPaymentMethodChoiceCommand command = createReadDefaultPaymentMethodCommand();
		command.execute();
	}

	private ReadDefaultPaymentMethodChoiceCommand createReadDefaultPaymentMethodCommand() {
		ReadDefaultPaymentMethodChoiceCommandImpl readDefaultPaymentMethodCommand =
				new ReadDefaultPaymentMethodChoiceCommandImpl(defaultPaymentMethodLookup, paymentMethodUriBuilderFactory,
						resourceOperationContext);

		ReadDefaultPaymentMethodChoiceCommandImpl.BuilderImpl builder =
				new ReadDefaultPaymentMethodChoiceCommandImpl.BuilderImpl(readDefaultPaymentMethodCommand);

		return builder
				.setScope(SCOPE)
				.setOrderUri(ORDER_URI)
				.build();
	}
}
