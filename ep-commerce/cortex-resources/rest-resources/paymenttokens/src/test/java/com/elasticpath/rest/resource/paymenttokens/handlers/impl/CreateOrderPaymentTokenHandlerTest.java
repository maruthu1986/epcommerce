/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.handlers.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.paymenttokens.integration.PaymentTokenWriterStrategy;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilderFactory;

/**
 * Tests the {@link CreateOrderPaymentTokenHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateOrderPaymentTokenHandlerTest {
	private static final String DECODED_ORDER_ID = "decodedOrderId";
	private static final String ENCODED_ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);
	private static final String PAYMENT_TOKEN_URI = "/paymentTokenUri";
	private static final String TEST_SCOPE = "testScope";

	@Mock
	private PaymentTokenWriterStrategy paymentTokenWriterStrategy;
	@Mock
	private OrderPaymentMethodUriBuilderFactory orderPaymentMethodUriBuilderFactory;
	@Mock
	private OrderPaymentMethodUriBuilder orderPaymentMethodUriBuilder;
	@InjectMocks
	private CreateOrderPaymentTokenHandler createOrderPaymentTokenHandler;

	private PaymentTokenEntity paymentTokenEntity;
	private ResourceState<OrderEntity> orderRepresentation;
	private PaymentTokenEntity createdPaymentTokenEntity;

	@Before
	public void setUpTestComponentsAndHappyCollaborators() {
		paymentTokenEntity = ResourceTypeFactory.createResourceEntity(PaymentTokenEntity.class);
		createdPaymentTokenEntity = ResourceTypeFactory.createResourceEntity(PaymentTokenEntity.class);


		orderRepresentation = ResourceState.Builder
				.create(OrderEntity.builder()
						.withOrderId(DECODED_ORDER_ID)
						.build())
				.withScope(TEST_SCOPE)
				.build();

		when(
				paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, DECODED_ORDER_ID, PaymentTokenOwnerType.ORDER_TYPE,
						TEST_SCOPE)).thenReturn(ExecutionResultFactory.createCreateOKWithData(createdPaymentTokenEntity, false));

		when(orderPaymentMethodUriBuilderFactory.get()).thenReturn(orderPaymentMethodUriBuilder);
		when(orderPaymentMethodUriBuilder.setScope(TEST_SCOPE)).thenReturn(orderPaymentMethodUriBuilder);
		when(orderPaymentMethodUriBuilder.setOrderId(ENCODED_ORDER_ID)).thenReturn(orderPaymentMethodUriBuilder);
		when(orderPaymentMethodUriBuilder.build()).thenReturn(PAYMENT_TOKEN_URI);
	}

	@Test
	public void ensureOrderReprentationClassIsOwnerRepresentationHandled() {
		assertEquals("The order representation should be the owner representation handled", OrdersMediaTypes.ORDER.id(),
				createOrderPaymentTokenHandler.handledOwnerRepresentationType());
	}

	@Test
	public void verifyPaymentTokenIsCreatedForOrder() {
		createOrderPaymentTokenHandler.createPaymentToken(paymentTokenEntity, orderRepresentation);

		verify(paymentTokenWriterStrategy, times(1)).createPaymentTokenForOwner(
				paymentTokenEntity,
				DECODED_ORDER_ID,
				PaymentTokenOwnerType.ORDER_TYPE, TEST_SCOPE
		);
	}

	@Test
	public void ensureValidPaymentTokenForOrderIsCreatedCorrectly() {
		ExecutionResult<PaymentTokenEntity> createResult = createOrderPaymentTokenHandler.createPaymentToken(paymentTokenEntity, orderRepresentation);

		assertExecutionResult(createResult)
				.data(createdPaymentTokenEntity);
	}

	@Test
	public void ensureReadOkWhenWriterStrategyReturnsReadOk() {
		when(paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, DECODED_ORDER_ID, PaymentTokenOwnerType.ORDER_TYPE,
						TEST_SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(createdPaymentTokenEntity));

		ExecutionResult<PaymentTokenEntity> createResult = createOrderPaymentTokenHandler.createPaymentToken(paymentTokenEntity, orderRepresentation);

		assertExecutionResult(createResult)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void ensureCreateOkWhenNewPaymentTokenIsAssociatedWithOwner() {
		ExecutionResult<PaymentTokenEntity> createResult = createOrderPaymentTokenHandler.createPaymentToken(paymentTokenEntity, orderRepresentation);

		assertExecutionResult(createResult)
				.resourceStatus(ResourceStatus.CREATE_OK);
	}

	@Test
	public void ensureNotFoundResultIsReturnedWhenOrderDoesNotExist() {
		when(
				paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, DECODED_ORDER_ID, PaymentTokenOwnerType.ORDER_TYPE,
						TEST_SCOPE)).thenReturn(ExecutionResultFactory.<PaymentTokenEntity> createNotFound());

		ExecutionResult<PaymentTokenEntity> createResult = createOrderPaymentTokenHandler.createPaymentToken(paymentTokenEntity, orderRepresentation);

		assertExecutionResult(createResult)
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureServerErrorReturnedWhenPaymentTokenFailsToBeAssociatedWithOrder() {
		when(
				paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, DECODED_ORDER_ID, PaymentTokenOwnerType.ORDER_TYPE,
						TEST_SCOPE)).thenReturn(ExecutionResultFactory.<PaymentTokenEntity> createServerError(""));

		ExecutionResult<PaymentTokenEntity> createResult = createOrderPaymentTokenHandler.createPaymentToken(paymentTokenEntity, orderRepresentation);

		assertExecutionResult(createResult)
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void verifyPaymentTokenUriForOrderIsBuilt() {
		createOrderPaymentTokenHandler.createPaymentTokenUri(createdPaymentTokenEntity, orderRepresentation);

		verify(orderPaymentMethodUriBuilder, times(1)).build();
	}

	@Test
	public void verifyOrderPaymentMethodUriBuilderIsInvokedWithCorrectId() {
		createOrderPaymentTokenHandler.createPaymentTokenUri(createdPaymentTokenEntity, orderRepresentation);

		verify(orderPaymentMethodUriBuilder, times(1)).setOrderId(ENCODED_ORDER_ID);
	}

	@Test
	public void verifyOrderPaymentMethodUriBuilderIsInvokedWithCorrectScope() {
		createOrderPaymentTokenHandler.createPaymentTokenUri(createdPaymentTokenEntity, orderRepresentation);

		verify(orderPaymentMethodUriBuilder, times(1)).setScope(TEST_SCOPE);
	}

	@Test
	public void ensurePaymentTokenUriForOrderIsReturnedCorrectly() {
		String createdPaymentTokenUri = createOrderPaymentTokenHandler.createPaymentTokenUri(createdPaymentTokenEntity, orderRepresentation);

		assertEquals("the created payment token uri should be the same as expected", PAYMENT_TOKEN_URI, createdPaymentTokenUri);
	}
}
