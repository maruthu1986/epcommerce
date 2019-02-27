/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.command.read.ReadResourceCommand;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodWriter;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Contains tests for ReadPaymentInfoCommandImpl execute method.
 */
@RunWith(MockitoJUnitRunner.class)
public final class SelectPaymentMethodCommandImplTest {

	private static final String SCOPE = "scope";
	private static final String PAYMENT_METHOD_ID = "payment-method-id";
	private static final String ORDER_ID = "orderId";
	private static final String PAYMENT_METHOD_URI = "/uri/...";
	private static final String ORDER_URI = "orderUri";
	private static final String PAYMENTMETHODS = "paymentmethods";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PaymentMethodWriter paymentMethodWriter;
	@Mock
	private PaymentMethodUriBuilder mockPaymentMethodUriBuilder;


	/**
	 * Tests that a valid payment selection can be made and returns a success execution result
	 * with required payment info data.
	 */
	@Test
	public void testValidSelectionOfPaymentMethod() {
		ExecutionResult<Boolean> paymentInfoWriterResult = ExecutionResultFactory.createCreateOKWithData(true, false);
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation(SCOPE, ORDER_ID, ORDER_URI);
		ResourceState<PaymentMethodEntity> paymentMethodsRepresentation = createPaymentMethodsRepresentation();

		when(paymentMethodWriter.updatePaymentMethodSelectionForOrder(SCOPE, ORDER_ID, PAYMENT_METHOD_ID))
				.thenReturn(paymentInfoWriterResult);

		SelectPaymentMethodCommandImpl selectPaymentInfoCommand =
				createSelectPaymentMethodsCommand(orderRepresentation,
						ExecutionResultFactory.<ResourceState<?>>createReadOK(paymentMethodsRepresentation));
		ExecutionResult<ResourceState<ResourceEntity>> executionResult = selectPaymentInfoCommand.execute();

		assertTrue(executionResult.isSuccessful());
		String expected = URIUtil.format(PAYMENTMETHODS, Selector.URI_PART, ORDER_URI);
		assertEquals("wrong uri", expected, ResourceStateUtil.getSelfUri(executionResult.getData()));
	}

	/**
	 * Tests error returned when read payment method fails.
	 */
	@Test(expected = BrokenChainException.class)
	public void testReadPaymentMethodsFails() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation(SCOPE, ORDER_ID, ORDER_URI);

		ExecutionResult<ResourceState<?>> notFoundResult = ExecutionResultFactory.createNotFound("not found");
		SelectPaymentMethodCommandImpl selectPaymentInfoCommand =
				createSelectPaymentMethodsCommand(orderRepresentation,
						notFoundResult);

		selectPaymentInfoCommand.execute();
	}


	/**
	 * Tests that a select payment execute where the call to updatePaymentMethodSelectionForOrder() fails returns a failure execution result.
	 */
	@Test
	public void testUpdatePaymentMethodSelectionForOrderFails() {
		ExecutionResult<Boolean> paymentInfoWriterResult = ExecutionResultFactory.createNotFound("Not created");
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation(SCOPE, ORDER_ID, ORDER_URI);
		ResourceState<PaymentMethodEntity> paymentMethodsRepresentation = createPaymentMethodsRepresentation();

		when(paymentMethodWriter.updatePaymentMethodSelectionForOrder(SCOPE, ORDER_ID, PAYMENT_METHOD_ID))
				.thenReturn(paymentInfoWriterResult);

		SelectPaymentMethodCommandImpl selectPaymentMethodCommand =
				createSelectPaymentMethodsCommand(orderRepresentation,
						ExecutionResultFactory.<ResourceState<?>>createReadOK(paymentMethodsRepresentation));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		selectPaymentMethodCommand.execute();
	}

	/**
	 * Creates select payment methods command with given parameters set onto it and basic classes wired in.
	 *
	 * @param expectedOrder the expected order representation.
	 * @param expectedPaymentMethodResult the expected payment method result
	 * @return Impl of the command.
	 */
	private SelectPaymentMethodCommandImpl createSelectPaymentMethodsCommand(final ResourceState<OrderEntity> expectedOrder,
			final ExecutionResult<ResourceState<?>> expectedPaymentMethodResult) {

		ReadResourceCommandBuilderProvider mockReadProvider = Mockito.mock(ReadResourceCommandBuilderProvider.class);
		PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory = Mockito.mock(PaymentMethodUriBuilderFactory.class);
		ReadResourceCommand paymentMethodsReadResourceCommand = Mockito.mock(ReadResourceCommand.class);
		SelectPaymentMethodCommandImpl selectPaymentInfoCommand =
				new SelectPaymentMethodCommandImpl(PAYMENTMETHODS, mockReadProvider, paymentMethodUriBuilderFactory, paymentMethodWriter);


		mockProvider(mockReadProvider, paymentMethodsReadResourceCommand);
		when(paymentMethodUriBuilderFactory.get())
				.thenReturn(mockPaymentMethodUriBuilder);
		when(mockPaymentMethodUriBuilder.setScope(SCOPE))
				.thenReturn(mockPaymentMethodUriBuilder);
		when(mockPaymentMethodUriBuilder.setPaymentMethodId(any(String.class)))
				.thenReturn(mockPaymentMethodUriBuilder);
		when(mockPaymentMethodUriBuilder.build())
				.thenReturn(PAYMENT_METHOD_URI);

		when(paymentMethodsReadResourceCommand.execute())
				.thenReturn(expectedPaymentMethodResult);

		SelectPaymentMethodCommandImpl.BuilderImpl builder = new SelectPaymentMethodCommandImpl.BuilderImpl(selectPaymentInfoCommand);
		builder.setPaymentMethodId(PAYMENT_METHOD_ID)
				.setScope(SCOPE)
				.setOrder(expectedOrder);

		return selectPaymentInfoCommand;
	}

	private void mockProvider(final ReadResourceCommandBuilderProvider readProvider,
			final ReadResourceCommand paymentMethodsReadResourceCommand) {

		ReadResourceCommand.Builder mockPaymentMethodsReadResourceCommandBuilder =
				Mockito.mock(ReadResourceCommand.Builder.class);

		when(readProvider.get())
				.thenReturn(mockPaymentMethodsReadResourceCommandBuilder);
		when(mockPaymentMethodsReadResourceCommandBuilder.setResourceUri(PAYMENT_METHOD_URI))
				.thenReturn(mockPaymentMethodsReadResourceCommandBuilder);
		when(mockPaymentMethodsReadResourceCommandBuilder.build())
				.thenReturn(paymentMethodsReadResourceCommand);
	}

	private ResourceState<OrderEntity> createOrderRepresentation(final String scope, final String orderId, final String orderUri) {
		Self self = SelfFactory.createSelf(orderUri, OrdersMediaTypes.ORDER.id());
		return ResourceState.Builder
				.create(OrderEntity.builder()
						.withOrderId(orderId)
						.build())
				.withSelf(self)
				.withScope(scope)
				.build();
	}

	private ResourceState<PaymentMethodEntity> createPaymentMethodsRepresentation() {
		return ResourceState.Builder
				.create(PaymentMethodEntity.builder().withPaymentMethodId(PAYMENT_METHOD_ID).build())
				.build();
	}
}
