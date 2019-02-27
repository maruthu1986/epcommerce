/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
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

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.integrations.payment.tokens.transformer.PaymentTokenTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;

/**
 * Tests the {@link CreateOrderPaymentTokenHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateOrderPaymentTokenHandlerTest {
	private static final String DECODED_ORDER_ID = "decodedOrderId";
	private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
	private static final String SCOPE = "SCOPE";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private PaymentTokenTransformer paymentTokenTransformer;
	@InjectMocks
	private CreateOrderPaymentTokenHandler createOrderPaymentTokenHandler;

	private CartOrder cartOrder = new CartOrderImpl();
	private PaymentTokenEntity paymentTokenEntity;
	private PaymentToken paymentToken;

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

		shouldFindCartOrderByGuid();
		shouldSaveCartOrder();
		shouldTransformToEntity();
	}

	@Test
	public void ensureOrderOwnerTypeIsHandled() {
		assertEquals("The order owner type should be handled", PaymentTokenOwnerType.ORDER_TYPE,
				createOrderPaymentTokenHandler.getHandledOwnerType());
	}

	@Test
	public void verifyCartOrderIsFoundByGuid() {
		createOrderPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_ORDER_ID, SCOPE);

		verify(cartOrderRepository, times(1)).findByGuid(SCOPE, DECODED_ORDER_ID);
	}


	@Test
	public void verifyCartOrderIsSaved() {
		createOrderPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_ORDER_ID, SCOPE);

		verify(cartOrderRepository, times(1)).saveCartOrder(cartOrder);
	}

	@Test
	public void verifyPaymentTokenIsTransformedToEntity() {
		createOrderPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_ORDER_ID, SCOPE);

		verify(paymentTokenTransformer, times(1)).transformToEntity(paymentToken);
	}

	@Test
	public void ensurePaymentTokenCanBeCreatedForValidOrder() {
		ExecutionResult<PaymentTokenEntity> result = createOrderPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_ORDER_ID, SCOPE);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.CREATE_OK)
				.data(paymentTokenEntity);
	}

	@Test
	public void ensureCartOrderIsNotSavedWhenPaymentTokenAlreadyExistsOnCartOrder() {
		cartOrder  = new CartOrderImpl();
		cartOrder.usePaymentMethod(paymentToken);

		shouldFindCartOrderByGuid();

		createOrderPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_ORDER_ID, SCOPE);

		verify(cartOrderRepository, never()).saveCartOrder(cartOrder);
	}

	@Test
	public void ensureNotFoundIsReturnedWhenCartOrderGuidIsInvalid() {
		when(cartOrderRepository.findByGuid(SCOPE, DECODED_ORDER_ID)).thenReturn(ExecutionResultFactory.<CartOrder>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		createOrderPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_ORDER_ID, SCOPE);
	}

	@Test
	public void ensureServerErrorIsReturnedWhenCartOrderFailsToSave() {
		when(cartOrderRepository.saveCartOrder(cartOrder)).thenReturn(ExecutionResultFactory.<CartOrder>createServerError(""));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		createOrderPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_ORDER_ID, SCOPE);
	}

	@Test
	public void ensurePaymentTokenIsNotReplacedWithEqualPaymentToken() {
		cartOrder  = new CartOrderImpl();
		cartOrder.usePaymentMethod(paymentToken);

		shouldFindCartOrderByGuid();

		ExecutionResult<PaymentTokenEntity> result = createOrderPaymentTokenHandler.createPaymentTokenForOwner(paymentToken, DECODED_ORDER_ID, SCOPE);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	private void shouldSaveCartOrder() {
		when(cartOrderRepository.saveCartOrder(cartOrder)).thenReturn(ExecutionResultFactory.createCreateOKWithData(cartOrder, true));
	}

	private void shouldFindCartOrderByGuid() {
		when(cartOrderRepository.findByGuid(SCOPE, DECODED_ORDER_ID)).thenReturn(ExecutionResultFactory.<CartOrder>createReadOK(cartOrder));
	}

	private void shouldTransformToEntity() {
		when(paymentTokenTransformer.transformToEntity(paymentToken)).thenReturn(paymentTokenEntity);
	}
}
