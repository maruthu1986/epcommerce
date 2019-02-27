/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.resource.paymenttokens.handlers.CreatePaymentTokenHandler;
import com.elasticpath.rest.resource.paymenttokens.handlers.registry.CreatePaymentTokenHandlerRegistry;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Tests the {@link PaymentTokenWriterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentTokenWriterImplTest {
	public static final String PAYMENT_TOKEN_URI = "/paymentTokenUri";
	public static final String TEST_CORRELATION_ID = "testCorrelationId";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private CreatePaymentTokenHandlerRegistry createPaymentTokenHandlerRegistry;
	@Mock
	private CreatePaymentTokenHandler<OrderEntity> createPaymentTokenHandler;
	@InjectMocks
	private PaymentTokenWriterImpl paymentTokenWriter;

	private ResourceState<OrderEntity> ownerRepresentation;

	private static final PaymentTokenEntity PAYMENT_TOKEN = PaymentTokenEntity.builder().build();
	private PaymentTokenEntity postedPaymentToken;
	private PaymentTokenEntity createdPaymentToken;

	@Before
	public void setupTestComponentsAndHappyCollaborators() {
		ownerRepresentation = ResourceState.Builder.create(OrderEntity.builder().build())
				.withSelf(SelfFactory.createSelf("/testSelfUri", OrdersMediaTypes.ORDER.id()))
				.build();
		postedPaymentToken = PaymentTokenEntity.builder()
				.build();
		createdPaymentToken = PaymentTokenEntity.builder()
				.withPaymentMethodId(TEST_CORRELATION_ID)
				.build();
		when(createPaymentTokenHandlerRegistry.lookupHandler(OrdersMediaTypes.ORDER.id())).thenReturn(createPaymentTokenHandler);
		when(createPaymentTokenHandler.createPaymentToken(postedPaymentToken, ownerRepresentation))
				.thenReturn(ExecutionResultFactory.createCreateOKWithData(createdPaymentToken, false));
		when(createPaymentTokenHandler.createPaymentTokenUri(createdPaymentToken, ownerRepresentation))
				.thenReturn(PAYMENT_TOKEN_URI);
	}

	@Test
	public void verifyCreatePaymentTokenHandlerIsLookedUp() {
		paymentTokenWriter.createPaymentToken(PAYMENT_TOKEN,
				ownerRepresentation);

		verify(createPaymentTokenHandlerRegistry, times(1)).lookupHandler(OrdersMediaTypes.ORDER.id());
	}

	@Test
	public void verifyPaymentTokenIsCreatedForOwner() {
		paymentTokenWriter.createPaymentToken(PAYMENT_TOKEN,
				ownerRepresentation);

		verify(createPaymentTokenHandler, times(1)).createPaymentToken(postedPaymentToken, ownerRepresentation);
	}

	@Test
	public void verifyCreatedPaymentTokenUriIsLookedUp() {
		paymentTokenWriter.createPaymentToken(PAYMENT_TOKEN,
				ownerRepresentation);

		verify(createPaymentTokenHandler, times(1)).createPaymentTokenUri(createdPaymentToken, ownerRepresentation);
	}

	@Test
	public void ensureNewPaymentTokenAssociatedWithOwnerReturnsCreateOk() {
		ExecutionResult<ResourceState<ResourceEntity>> result = paymentTokenWriter.createPaymentToken(PAYMENT_TOKEN,
				ownerRepresentation);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.CREATE_OK);
	}

	@Test
	public void ensureExistingPaymentTokenCreatedForOwnerReturnsReadOk() {
		when(createPaymentTokenHandler.createPaymentToken(postedPaymentToken, ownerRepresentation))
				.thenReturn(ExecutionResultFactory.createCreateOKWithData(createdPaymentToken, true));

		ExecutionResult<ResourceState<ResourceEntity>> result = paymentTokenWriter.createPaymentToken(PAYMENT_TOKEN,
				ownerRepresentation);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void ensureValidPaymentTokenForOwnerIsCreatedCorrectly() {
		ExecutionResult<ResourceState<ResourceEntity>> result = paymentTokenWriter.createPaymentToken(PAYMENT_TOKEN,
				ownerRepresentation);

		assertExecutionResult(result)
				.data(createExpectedCreateRepresentation());
	}

	@Test
	public void ensureNotFoundReturnedWhenOwnerIsNotFound() {
		when(createPaymentTokenHandler.createPaymentToken(postedPaymentToken, ownerRepresentation))
				.thenReturn(ExecutionResultFactory.<PaymentTokenEntity>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentTokenWriter.createPaymentToken(PAYMENT_TOKEN,
				ownerRepresentation);
	}

	@Test
	public void ensureServerErrorReturnsWhenPaymentTokenFailsToBeAssociatedWithOwner() {
		when(createPaymentTokenHandler.createPaymentToken(postedPaymentToken, ownerRepresentation))
				.thenReturn(ExecutionResultFactory.<PaymentTokenEntity>createServerError(""));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		paymentTokenWriter.createPaymentToken(PAYMENT_TOKEN,
				ownerRepresentation);
	}

	private ResourceState<ResourceEntity> createExpectedCreateRepresentation() {
		return ResourceState.builder()
				.withSelf(SelfFactory.createSelf(PAYMENT_TOKEN_URI))
				.build();
	}
}
