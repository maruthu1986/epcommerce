/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.integration.epcommerce.impl;

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

import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.integrations.payment.tokens.handler.CreatePaymentTokenHandler;
import com.elasticpath.rest.integrations.payment.tokens.handler.registry.CreatePaymentTokenHandlerRegistry;
import com.elasticpath.rest.integrations.payment.tokens.transformer.PaymentTokenTransformer;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;

/**
 * Tests the {@link PaymentTokenWriterStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentTokenWriterStrategyImplTest {
	private static final String DECODED_OWNER_ID = "decodedOwnerId";
	private static final String PAYMENT_TOKEN_ID = "paymentTokenId";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private PaymentTokenTransformer paymentTokenTransformer;
	@Mock
	private CreatePaymentTokenHandlerRegistry createPaymentTokenHandlerRegistry;
	@Mock
	private CreatePaymentTokenHandler createPaymentTokenHandler;
	@InjectMocks
	private PaymentTokenWriterStrategyImpl paymentTokenWriterStrategy;

	private PaymentTokenEntity paymentTokenEntity;
	private PaymentToken paymentToken;
	private static final String SCOPE = "SCOPE";

	/**
	 * Sets up mock collaborators with happy path expectations.
	 */
	@Before
	public void setUpHappyCollaboratorsAndComponents() {
		paymentTokenEntity = PaymentTokenEntity.builder()
				.withPaymentMethodId(PAYMENT_TOKEN_ID)
				.build();
		paymentToken = new PaymentTokenImpl.TokenBuilder()
						.build();

		when(createPaymentTokenHandlerRegistry.lookupHandler(PaymentTokenOwnerType.ORDER_TYPE)).thenReturn(createPaymentTokenHandler);
		when(createPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_OWNER_ID, SCOPE))
				.thenReturn(ExecutionResultFactory.createCreateOKWithData(paymentTokenEntity, false));
		when(paymentTokenTransformer.transformToDomain(paymentTokenEntity)).thenReturn(paymentToken);
	}

	@Test
	public void verifyPaymentTokenEntityIsTransformedToDomain() {
		paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, DECODED_OWNER_ID, PaymentTokenOwnerType.ORDER_TYPE, SCOPE);

		verify(paymentTokenTransformer, times(1)).transformToDomain(paymentTokenEntity);
	}

	@Test
	public void verifyCreatePaymentTokenHandlerIsLookedUp() {
		paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, DECODED_OWNER_ID, PaymentTokenOwnerType.ORDER_TYPE, SCOPE);

		verify(createPaymentTokenHandlerRegistry, times(1)).lookupHandler(PaymentTokenOwnerType.ORDER_TYPE);
	}

	@Test
	public void verifyPaymentTokenIsCreated() {
		paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, DECODED_OWNER_ID, PaymentTokenOwnerType.ORDER_TYPE, SCOPE);

		verify(createPaymentTokenHandler, times(1)).createPaymentTokenForOwner(paymentToken, DECODED_OWNER_ID, SCOPE);
	}

	@Test
	public void ensureServerErrorIsReturnedWhenNullPaymentTokenIsPassed() {
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		paymentTokenWriterStrategy.createPaymentTokenForOwner(null, DECODED_OWNER_ID, null, SCOPE);
	}

	@Test
	public void ensureServerErrorIsReturnedWhenNullOwnerIdIsPassed() {
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, null, null, SCOPE);
	}

	@Test
	public void ensurePaymentTokenCanBeCreatedForValidOwner() {
		ExecutionResult<PaymentTokenEntity> result = paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, DECODED_OWNER_ID,
				PaymentTokenOwnerType.ORDER_TYPE, SCOPE);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.CREATE_OK)
				.data(paymentTokenEntity);
	}

	@Test
	public void ensureNotFoundIsReturnedWhenDecodedOwnerIdIsInvalid() {
		when(createPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_OWNER_ID, SCOPE))
				.thenReturn(ExecutionResultFactory.<PaymentTokenEntity>createNotFound());

		ExecutionResult<PaymentTokenEntity> result = paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, DECODED_OWNER_ID,
				PaymentTokenOwnerType.ORDER_TYPE, SCOPE);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureServerErrorIsReturnedWhenHandlerFailsToCreatePaymentToken() {
		when(createPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_OWNER_ID, SCOPE))
				.thenReturn(ExecutionResultFactory.<PaymentTokenEntity>createServerError(""));

		ExecutionResult<PaymentTokenEntity> result = paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity, DECODED_OWNER_ID,
				PaymentTokenOwnerType.ORDER_TYPE, SCOPE);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}
}
