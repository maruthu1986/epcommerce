/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.resource.commons.handler.registry.PaymentHandlerRegistry;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.integration.PaymentMethodLookupStrategy;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.resource.paymentmethods.transformer.OrderPaymentMethodTransformer;
import com.elasticpath.rest.resource.paymentmethods.transformer.PaymentMethodTransformer;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.test.AssertExecutionResult;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Basic smoke test of {@link PaymentMethodLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public final class PaymentMethodLookupImplTest {
	private static final String DECODED_ORDER_ID = "decodedOrderId";
	private static final String ORDER_ID = Base32Util.encode(DECODED_ORDER_ID);
	private static final String SCOPE = "scope";
	private static final String USER_ID = "user";
	private static final String DECODED_VALID_PAYMENT_METHOD_ID = "mockValidPaymentId";
	private static final String VALID_PAYMENT_METHOD_ID = Base32Util.encode(DECODED_VALID_PAYMENT_METHOD_ID);
	private static final String DECODED_CREDIT_CARD_ID_TWO = "266f3a06-d6c3-4aa9-9985-6e1ba73a7565";
	private static final String DECODED_CREDIT_CARD_ID_ONE = "3c76e2df-f0af-4f8e-ace3-415b30060f27";
	private static final String RESOURCE_SERVER_NAME = "paymentmethods";
	private static final String TEST_REPRESENTATION_TYPE = "testRepresentationType";
	private static final String SELECTED_PAYMENT_METHOD_URI = "/selectedPaymentMethodUri";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PaymentMethodLookupStrategy mockPaymentMethodLookupStrategy;
	@Mock
	private PaymentMethodTransformer mockPaymentMethodTransformer;
	@Mock
	private OrderPaymentMethodTransformer orderPaymentMethodTransformer;
	@Mock
	private PaymentHandlerRegistry paymentMethodHandlerRegistry;
	@Mock
	private PaymentHandler paymentMethodHandler;
	@Mock
	private PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory;
	@Mock
	private OrderPaymentMethodUriBuilderFactory orderPaymentMethodUriBuilderFactory;
	@Mock
	private OrderPaymentMethodUriBuilder orderPaymentMethodUriBuilder;

	private PaymentMethodLookup paymentMethodsLookup;
	private ResourceState<OrderEntity> orderRepresentation;
	private ResourceState<PaymentMethodEntity> paymentMethodRepresentation;

	@Before
	public void setUpPaymentMethodLookup() {
		when(paymentMethodUriBuilderFactory.get()).thenReturn(new PaymentMethodUriBuilderImpl(RESOURCE_SERVER_NAME));
		paymentMethodsLookup = new PaymentMethodLookupImpl(
				paymentMethodUriBuilderFactory,
				mockPaymentMethodLookupStrategy,
				mockPaymentMethodTransformer,
				orderPaymentMethodTransformer,
				paymentMethodHandlerRegistry,
				orderPaymentMethodUriBuilderFactory);
		orderRepresentation = ResourceState.Builder
				.create(OrderEntity.builder().withOrderId(DECODED_ORDER_ID).build())
				.withScope(SCOPE)
				.build();

		paymentMethodRepresentation = ResourceState.Builder
				.create(PaymentMethodEntity.builder().build())
				.withScope(SCOPE)
				.build();

		shouldBuildSelectedPaymentMethodUri();
	}

	/**
	 * Test getting selected payment method id for order with valid order id.
	 */
	@Test
	public void testGettingSelectedPaymentMethodIdForOrder() {
		PaymentMethodEntity paymentMethodEntity = PaymentMethodEntity.builder()
				.withPaymentMethodId(DECODED_CREDIT_CARD_ID_ONE)
				.build();
		shouldFindChosenPaymentMethodWithPaymentMethod(paymentMethodEntity);

		ExecutionResult<String> result = paymentMethodsLookup.findChosenPaymentMethodIdForOrder(SCOPE, USER_ID, DECODED_ORDER_ID);

		assertTrue("Read should be successful.", result.isSuccessful());
		assertEquals("selected payment method should be the same", DECODED_CREDIT_CARD_ID_ONE, result.getData());
	}

	/**
	 * Test getting selected payment method id for order when order not found.
	 */
	@Test
	public void testGettingSelectedPaymentMethodIdForOrderWhenOrderNotFound() {
		shouldNotFindSelectedPaymentMethodForOrder();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodsLookup.findChosenPaymentMethodIdForOrder(SCOPE, USER_ID, DECODED_ORDER_ID);
	}

	@Test
	public void ensureFindSelectedPaymentMethodForOrderLooksUpPaymentMethodForOrder() {
		shouldFindSelectedPaymentMethodForOrderWithPaymentMethod(ResourceTypeFactory.createResourceEntity(PaymentMethodEntity.class));
		paymentMethodsLookup.findSelectedPaymentMethodForOrder(orderRepresentation);

		verify(mockPaymentMethodLookupStrategy, times(1)).getOrderPaymentMethod(SCOPE, DECODED_ORDER_ID);
	}

	@Test
	public void ensureFindSelectedPaymentMethodForOrderTransformsSelectedPaymentMethod() {
		PaymentMethodEntity paymentMethodEntity = ResourceTypeFactory.createResourceEntity(PaymentMethodEntity.class);
		shouldFindSelectedPaymentMethodForOrderWithPaymentMethod(paymentMethodEntity);

		paymentMethodsLookup.findSelectedPaymentMethodForOrder(orderRepresentation);

		verify(orderPaymentMethodTransformer, times(1)).transformToRepresentation(orderRepresentation, paymentMethodEntity);
	}

	@Test
	public void ensureSelectedPaymentMethodRepresentationForValidOrderCanBeFoundSuccessfully() {
		PaymentMethodEntity paymentMethodEntity = ResourceTypeFactory.createResourceEntity(PaymentMethodEntity.class);
		shouldFindSelectedPaymentMethodForOrderWithPaymentMethod(paymentMethodEntity);
		shouldTransformPaymentMethodForOrder(paymentMethodEntity);

		ExecutionResult<ResourceState<PaymentMethodEntity>> result = paymentMethodsLookup.findSelectedPaymentMethodForOrder(orderRepresentation);

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(paymentMethodRepresentation);
	}

	@Test
	public void ensureFindSelectedPaymentMethodForOrderReturnsNotFoundWhenStrategyReturnsNotFound() {
		shouldNotFindSelectedPaymentMethodForOrder();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodsLookup.findSelectedPaymentMethodForOrder(orderRepresentation);

	}

	@Test
	public void ensureFindSelectedPaymentMethodForOrderReturnsServerErrorWhenStrategyReturnsServerError() {
		when(mockPaymentMethodLookupStrategy.getOrderPaymentMethod(SCOPE, DECODED_ORDER_ID))
				.thenReturn(ExecutionResultFactory.<PaymentMethodEntity>createServerError(""));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		paymentMethodsLookup.findSelectedPaymentMethodForOrder(orderRepresentation);
	}

	@Test
	public void ensureScopeIsSetWhenConstructingSelectedPaymentMethodUri() {
		PaymentMethodEntity paymentMethod = PaymentMethodEntity.builder()
				.withPaymentMethodId(DECODED_VALID_PAYMENT_METHOD_ID)
				.build();

		shouldFindSelectedPaymentMethodForOrderWithPaymentMethod(paymentMethod);
		shouldFindPaymentMethodHandlerForPaymentMethod(paymentMethod);
		shouldPaymentMethodHandlerReturnRepresentationTypeWithValue(TEST_REPRESENTATION_TYPE);

		paymentMethodsLookup.getSelectedPaymentMethodLinkForOrder(SCOPE, DECODED_ORDER_ID);

		verify(orderPaymentMethodUriBuilder, times(1)).setScope(SCOPE);
	}

	@Test
	public void ensureOrderIdIsSetWhenConstructingSelectedPaymentMethodUri() {
		PaymentMethodEntity paymentMethod = PaymentMethodEntity.builder()
				.withPaymentMethodId(DECODED_VALID_PAYMENT_METHOD_ID)
				.build();

		shouldFindSelectedPaymentMethodForOrderWithPaymentMethod(paymentMethod);
		shouldFindPaymentMethodHandlerForPaymentMethod(paymentMethod);
		shouldPaymentMethodHandlerReturnRepresentationTypeWithValue(TEST_REPRESENTATION_TYPE);

		paymentMethodsLookup.getSelectedPaymentMethodLinkForOrder(SCOPE, DECODED_ORDER_ID);

		verify(orderPaymentMethodUriBuilder, times(1)).setOrderId(ORDER_ID);
	}

	@Test
	public void ensureSelectedPaymentMethodUriIsBuilt() {
		PaymentMethodEntity paymentMethod = PaymentMethodEntity.builder()
				.withPaymentMethodId(DECODED_VALID_PAYMENT_METHOD_ID)
				.build();

		shouldFindSelectedPaymentMethodForOrderWithPaymentMethod(paymentMethod);
		shouldFindPaymentMethodHandlerForPaymentMethod(paymentMethod);
		shouldPaymentMethodHandlerReturnRepresentationTypeWithValue(TEST_REPRESENTATION_TYPE);

		paymentMethodsLookup.getSelectedPaymentMethodLinkForOrder(SCOPE, DECODED_ORDER_ID);

		verify(orderPaymentMethodUriBuilder, times(1)).build();
	}

	/**
	 * Tests whether a payment method is required.
	 */
	@Test
	public void testIsPaymentMethodRequired() {
		when(mockPaymentMethodLookupStrategy.isPaymentRequired(SCOPE, DECODED_ORDER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(Boolean.TRUE));

		ExecutionResult<Boolean> result = paymentMethodsLookup.isPaymentRequired(SCOPE, DECODED_ORDER_ID);
		assertTrue("Read should be successful.", result.isSuccessful());
		assertTrue("Payment should be required", result.getData());
	}

	/**
	 * Testing happy path getting a valid credit card.
	 */
	@Test
	public void testGetValidPaymentMethod() {
		PaymentMethodEntity paymentMethodEntity = ResourceTypeFactory.createResourceEntity(PaymentMethodEntity.class);

		when(mockPaymentMethodLookupStrategy.getPaymentMethod(SCOPE, DECODED_VALID_PAYMENT_METHOD_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(paymentMethodEntity));

		when(mockPaymentMethodTransformer.transformToRepresentation(SCOPE, paymentMethodEntity))
				.thenReturn(ResourceState.Builder.create(paymentMethodEntity).build());

		ExecutionResult<ResourceState<PaymentMethodEntity>> paymentMethodRead = paymentMethodsLookup.getPaymentMethod(SCOPE,
				VALID_PAYMENT_METHOD_ID);
		assertTrue("Read should be successful.", paymentMethodRead.isSuccessful());
		assertNull("Error message should not be present.", paymentMethodRead.getErrorMessage());
	}

	/**
	 * Testing getting a non-existent credit card.
	 */
	@Test
	public void testGetInvalidPaymentMethod() {
		when(mockPaymentMethodLookupStrategy.getPaymentMethod(SCOPE, DECODED_VALID_PAYMENT_METHOD_ID))
				.thenReturn(ExecutionResultFactory.<PaymentMethodEntity>createNotFound("Credit card not found."));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		ExecutionResult<ResourceState<PaymentMethodEntity>> paymentMethodRead = paymentMethodsLookup.getPaymentMethod(SCOPE,
				VALID_PAYMENT_METHOD_ID);
		assertTrue(paymentMethodRead.isFailure());
		String errMsg = paymentMethodRead.getErrorMessage();
		assertThat(errMsg, containsString("Credit card not found"));
	}

	/**
	 * Test getting a valid list of credit card ids.
	 */
	@Test
	public void testGetValidListOfCreditCardIds() {

		Collection<String> paymentMethodLookupResult = Arrays.asList(DECODED_CREDIT_CARD_ID_ONE, DECODED_CREDIT_CARD_ID_TWO);

		when(mockPaymentMethodLookupStrategy.getPaymentMethodIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(paymentMethodLookupResult));

		ExecutionResult<Collection<String>> paymentMethodIdsReadResult =
				paymentMethodsLookup.getPaymentMethodIds(SCOPE, USER_ID);

		String paymentMethodIdOne = Base32Util.encode(DECODED_CREDIT_CARD_ID_ONE);
		String paymentMethodIdTwo = Base32Util.encode(DECODED_CREDIT_CARD_ID_TWO);

		assertTrue("Payment methods read should be successful. ", paymentMethodIdsReadResult.isSuccessful());
		Collection<String> paymentMethodIds = paymentMethodIdsReadResult.getData();
		assertNotNull("Entity should not be null.", paymentMethodIds);
		assertThat(paymentMethodIds, Matchers.containsInAnyOrder(paymentMethodIdOne, paymentMethodIdTwo));
	}

	/**
	 * Attempt to get credit card ids when none found.
	 */
	@Test
	public void testGetListOfCreditCardIdsWhenNoCreditCardsFound() {
		String cardsNotFoundErrorMessage = "Cards not found";

		when(mockPaymentMethodLookupStrategy.getPaymentMethodIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound(cardsNotFoundErrorMessage));

		ExecutionResult<Collection<String>> paymentMethodIdsReadResult =
				paymentMethodsLookup.getPaymentMethodIds(SCOPE, USER_ID);
		assertThat("Error should indicate no cards were found. ", paymentMethodIdsReadResult.getErrorMessage(),
				containsString(cardsNotFoundErrorMessage));

		assertNull("Result should be null.", paymentMethodIdsReadResult.getData());
	}

	@Test
	public void ensureSuccessfulRetrievalOfPaymentMethodLinksForCustomer() {
		PaymentMethodEntity paymentMethod = PaymentMethodEntity.builder()
				.withPaymentMethodId(DECODED_VALID_PAYMENT_METHOD_ID)
				.build();

		Collection<PaymentMethodEntity> paymentMethodEntities = Collections.singleton(paymentMethod);
		when(mockPaymentMethodLookupStrategy.getPaymentMethodsForUser(SCOPE, USER_ID)).thenReturn(
				ExecutionResultFactory.createReadOK(paymentMethodEntities));

		shouldFindPaymentMethodHandlerForPaymentMethod(paymentMethod);
		shouldPaymentMethodHandlerReturnRepresentationTypeWithValue(TEST_REPRESENTATION_TYPE);

		String paymentMethodUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, VALID_PAYMENT_METHOD_ID);
		ResourceLink expectedPaymentMethodLink = ElementListFactory.createElementOfList(paymentMethodUri, TEST_REPRESENTATION_TYPE);

		ExecutionResult<Collection<ResourceLink>> paymentMethodLinksForUser = paymentMethodsLookup.getPaymentMethodLinksForUser(SCOPE, USER_ID);

		AssertExecutionResult.assertExecutionResult(paymentMethodLinksForUser)
				.isSuccessful()
				.data(Arrays.asList(expectedPaymentMethodLink));
	}

	@Test
	public void ensureSuccessfulRetrievalOfSelectedPaymentMethodLinkForOrder() {
		PaymentMethodEntity paymentMethod = PaymentMethodEntity.builder()
				.withPaymentMethodId(DECODED_VALID_PAYMENT_METHOD_ID)
				.build();

		shouldFindSelectedPaymentMethodForOrderWithPaymentMethod(paymentMethod);
		shouldFindPaymentMethodHandlerForPaymentMethod(paymentMethod);
		shouldPaymentMethodHandlerReturnRepresentationTypeWithValue(TEST_REPRESENTATION_TYPE);


		ResourceLink expectedSelectedPAymentMethodLink = ResourceLinkFactory.createNoRev(SELECTED_PAYMENT_METHOD_URI, TEST_REPRESENTATION_TYPE,
				PaymentMethodRels.PAYMENTMETHOD_REL);

		ExecutionResult<ResourceLink> result = paymentMethodsLookup.getSelectedPaymentMethodLinkForOrder(SCOPE, DECODED_ORDER_ID);

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(expectedSelectedPAymentMethodLink);
	}

	@Test
	public void ensureLinkReturnedFromGetSelectorChosenPaymentMethodLinkWhenPaymentMethodNotFound() {
		shouldFindSelectorChosenPaymentMethodForOrderWithPaymentMethod(createPaymentMethodWithCorrelationId(DECODED_VALID_PAYMENT_METHOD_ID));
		shouldFindPaymentMethodHandlerForPaymentMethod(createPaymentMethodWithCorrelationId(DECODED_VALID_PAYMENT_METHOD_ID));
		shouldPaymentMethodHandlerReturnRepresentationTypeWithValue(TEST_REPRESENTATION_TYPE);

		ExecutionResult<ResourceLink> result = paymentMethodsLookup.getSelectorChosenPaymentMethodLink(SCOPE, USER_ID, DECODED_ORDER_ID);

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(createPaymentMethodLinkWithPaymentMethodId(VALID_PAYMENT_METHOD_ID));
	}


	@Test
	public void ensureNotFoundReturnedForRetrievalOfSelectedPaymentMethodLinkForOrderWithNoPaymentMethodSelected() {
		shouldNotFindSelectedPaymentMethodForOrder();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodsLookup.getSelectedPaymentMethodLinkForOrder(SCOPE, DECODED_ORDER_ID);
	}

	@Test
	public void ensureNotFoundReturnedFromGetSelectorChosenPaymentMethodLinkWhenPaymentMethodNotFound() {
		shouldNotFindSelectedPaymentMethodForOrder();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodsLookup.getSelectorChosenPaymentMethodLink(SCOPE, USER_ID, DECODED_ORDER_ID);
	}

	private void shouldNotFindSelectedPaymentMethodForOrder() {
		when(mockPaymentMethodLookupStrategy.getOrderPaymentMethod(SCOPE, DECODED_ORDER_ID)).thenReturn(
				ExecutionResultFactory.<PaymentMethodEntity>createNotFound());

		when(mockPaymentMethodLookupStrategy.getSelectorChosenPaymentMethod(SCOPE, USER_ID, DECODED_ORDER_ID)).thenReturn(
				ExecutionResultFactory.<PaymentMethodEntity>createNotFound());
	}

	private void shouldFindPaymentMethodHandlerForPaymentMethod(final PaymentMethodEntity paymentMethod) {
		when(paymentMethodHandlerRegistry.lookupHandler(paymentMethod)).thenReturn(paymentMethodHandler);
	}

	private void shouldPaymentMethodHandlerReturnRepresentationTypeWithValue(final String representationType) {
		when(paymentMethodHandler.representationType()).thenReturn(representationType);
	}

	private void shouldFindSelectedPaymentMethodForOrderWithPaymentMethod(final PaymentMethodEntity paymentMethod) {
		when(mockPaymentMethodLookupStrategy.getOrderPaymentMethod(SCOPE, DECODED_ORDER_ID)).thenReturn(
				ExecutionResultFactory.createReadOK(paymentMethod));
	}

	private void shouldFindSelectorChosenPaymentMethodForOrderWithPaymentMethod(final PaymentMethodEntity paymentMethod) {
		when(mockPaymentMethodLookupStrategy.getSelectorChosenPaymentMethod(SCOPE, USER_ID, DECODED_ORDER_ID)).thenReturn(
				ExecutionResultFactory.createReadOK(paymentMethod));
	}

	private void shouldTransformPaymentMethodForOrder(final PaymentMethodEntity paymentMethodEntity) {
		when(orderPaymentMethodTransformer.transformToRepresentation(orderRepresentation, paymentMethodEntity))
				.thenReturn(paymentMethodRepresentation);
	}

	private void shouldBuildSelectedPaymentMethodUri() {
		when(orderPaymentMethodUriBuilderFactory.get()).thenReturn(orderPaymentMethodUriBuilder);
		when(orderPaymentMethodUriBuilder.setOrderId(ORDER_ID)).thenReturn(orderPaymentMethodUriBuilder);
		when(orderPaymentMethodUriBuilder.setScope(SCOPE)).thenReturn(orderPaymentMethodUriBuilder);
		when(orderPaymentMethodUriBuilder.build()).thenReturn(SELECTED_PAYMENT_METHOD_URI);
	}

	private PaymentMethodEntity createPaymentMethodWithCorrelationId(final String correlationId) {
		return PaymentMethodEntity.builder()
				.withPaymentMethodId(correlationId)
				.build();
	}

	private ResourceLink createPaymentMethodLinkWithPaymentMethodId(final String paymentMethodId) {
		String paymentMethodUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, paymentMethodId);
		return ResourceLinkFactory.createNoRev(paymentMethodUri, TEST_REPRESENTATION_TYPE,
				PaymentMethodRels.PAYMENTMETHOD_REL);
	}
	private void shouldFindChosenPaymentMethodWithPaymentMethod(final PaymentMethodEntity paymentMethodEntity) {
		when(mockPaymentMethodLookupStrategy.getSelectorChosenPaymentMethod(SCOPE, USER_ID, DECODED_ORDER_ID))
			.thenReturn(ExecutionResultFactory.createReadOK(paymentMethodEntity));
	}


}
