/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.domain.customer.impl.AbstractPaymentMethodImpl;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TotalsCalculator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.CustomerPaymentMethodsRepository;
import com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.transform.PaymentMethodTransformer;

/**
 * Test class for {@link PaymentMethodLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public class PaymentMethodLookupStrategyImplTest {

	private static final String RESULT_SHOULD_BE_SUCCESSFUL = "result should be successful";
	private static final String ORDER_GUID = "orderGuid";
	private static final String STORE_CODE = "storeCode";
	private static final String CUSTOMER_GUID = "customer guid";
	private static final String PAYMENT_METHOD_ID = "test payment method id";
	private static final String PAYMENT_METHODS_NOT_FOUND = "Payment methods not found";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private CustomerPaymentMethodsRepository customerPaymentMethodsRepository;
	@Mock
	private PaymentMethodTransformer paymentMethodTransformer;
	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private TotalsCalculator totalsCalculator;
	@Mock
	private AbstractPaymentMethodImpl abstractPaymentMethod;
	@Mock
	private CustomerPaymentMethods customerPaymentMethods;
	@Mock
	private CartOrder cartOrder;
	@InjectMocks
	private PaymentMethodLookupStrategyImpl paymentMethodLookupStrategy;


	/**
	 * Tests getting a selected payment method for an order with valid order id and storecode.
	 */
	@Test
	public void testGetSelectedPaymentMethodForOrder() {
		final CartOrder cartOrder = createCartOrderWithPaymentMethod();
		final PaymentMethodEntity expectedPaymentMethod = ResourceTypeFactory.createResourceEntity(PaymentMethodEntity.class);

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		shouldTransformToEntity(cartOrder.getPaymentMethod(), expectedPaymentMethod);
		ExecutionResult<PaymentMethodEntity> result = paymentMethodLookupStrategy.getOrderPaymentMethod(STORE_CODE, ORDER_GUID);

		assertExecutionResult(result)
				.isSuccessful()
				.data(expectedPaymentMethod);
	}

	/**
	 * Tests getting a selected payment method for an order when cart order not found.
	 */
	@Test
	public void testGetSelectedPaymentMethodForOrderWhenCartOrderNotFound() {
		shouldFindByGuidWithResult(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.getOrderPaymentMethod(STORE_CODE, ORDER_GUID);
	}

	/**
	 * Tests checking whether a payment method is selected for a cart order.
	 */
	@Test
	public void testIsPaymentMethodSelectedForOrder() {
		final CartOrder cartOrder = createCartOrderWithPaymentMethod();

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));

		ExecutionResult<Boolean> result = paymentMethodLookupStrategy.isPaymentMethodSelectedForOrder(STORE_CODE, ORDER_GUID);

		assertExecutionResult(result)
							.isSuccessful()
							.data(Boolean.TRUE);
	}

	/**
	 * Tests checking whether a payment method is selected for a cart order with no payment method.
	 */
	@Test
	public void testIsPaymentMethodSelectedForOrderWhenNoPaymentMethodOnCartOrder() {
		final CartOrder cartOrder = new CartOrderImpl();

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));

		ExecutionResult<Boolean> result = paymentMethodLookupStrategy.isPaymentMethodSelectedForOrder(STORE_CODE, ORDER_GUID);

		assertExecutionResult(result)
				.isSuccessful()
				.data(Boolean.FALSE);
	}

	/**
	 * Tests getting a selected payment method for an order with no associated payment methods.
	 */
	@Test
	public void testGetSelectedPaymentMethodForOrderWithNoPaymentMethods() {
		CartOrder cartOrder = new CartOrderImpl();

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		ExecutionResult<PaymentMethodEntity> result = paymentMethodLookupStrategy.getOrderPaymentMethod(STORE_CODE, ORDER_GUID);

		assertThat(result.getErrorMessage(), containsString("No payment method set on cart order"));
	}

	/**
	 * Tests checking whether a payment is required with a valid order id.
	 */
	@Test
	public void testIsPaymentRequiredWithValidOrderId() {
		Money money = Money.valueOf(BigDecimal.TEN, Currency.getInstance("CAD"));
		shouldCalculateTotal(ExecutionResultFactory.createReadOK(money));

		ExecutionResult<Boolean> result = paymentMethodLookupStrategy.isPaymentRequired(STORE_CODE, ORDER_GUID);
		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertTrue("A payment should be required", result.getData());
	}

	@Test
	public void testIsPaymentRequiredWhenTotalCalculationFails() {
		shouldCalculateTotal(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.isPaymentRequired(STORE_CODE, ORDER_GUID);
	}

	/**
	 * Test get payment method ids by customer.
	 */
	@Test
	public void testGetPaymentMethodIdsByCustomerGuid() {
		PaymentMethod paymentMethod = new PaymentMethod() { };
		Collection<PaymentMethod> expectedPaymentMethodsForCustomer = Arrays.asList(paymentMethod);

		PaymentMethodEntity paymentMethodEntity = PaymentMethodEntity.builder()
				.withPaymentMethodId(PAYMENT_METHOD_ID)
				.build();


		when(customerPaymentMethods.all()).thenReturn(expectedPaymentMethodsForCustomer);
		shouldFindPaymentMethodsForCustomerGuidWithResult(ExecutionResultFactory.createReadOK(customerPaymentMethods));
		shouldTransformToEntity(paymentMethod, paymentMethodEntity);
		setupUserIdentifier(CUSTOMER_GUID);

		ExecutionResult<Collection<String>> result = paymentMethodLookupStrategy.getPaymentMethodIds(STORE_CODE, CUSTOMER_GUID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertThat(result.getData(), hasItem(PAYMENT_METHOD_ID));
	}

	/**
	 * Test the behaviour of get payment method ids by customer with repository failure.
	 */
	@Test
	public void testGetPaymentMethodIdsByCustomerGuidWithRepositoryFailure() {
		shouldFindPaymentMethodsForCustomerGuidWithResult(
				ExecutionResultFactory.createNotFound(PAYMENT_METHODS_NOT_FOUND));
		setupUserIdentifier(CUSTOMER_GUID);

		paymentMethodLookupStrategy.getPaymentMethodIds(STORE_CODE, CUSTOMER_GUID);
	}

	/**
	 * Tests the behaviour of successfully getting payment methods for a user.
	 */
	@Test
	public void testGetPaymentMethodsForUser() {
		PaymentMethod paymentMethod = new PaymentMethod() { };

		PaymentMethodEntity paymentMethodEntity = PaymentMethodEntity.builder()
				.withPaymentMethodId(PAYMENT_METHOD_ID)
				.build();

		Collection<PaymentMethod> expectedPaymentMethodsForCustomer = Arrays.asList(paymentMethod);

		when(customerPaymentMethods.all()).thenReturn(expectedPaymentMethodsForCustomer);
		shouldFindPaymentMethodsForCustomerGuidWithResult(ExecutionResultFactory.createReadOK(customerPaymentMethods));
		shouldTransformToEntity(paymentMethod, paymentMethodEntity);

		setupUserIdentifier(CUSTOMER_GUID);

		ExecutionResult<Collection<PaymentMethodEntity>> result = paymentMethodLookupStrategy.getPaymentMethodsForUser(STORE_CODE, CUSTOMER_GUID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertThat(result.getData(), hasItem(paymentMethodEntity));
	}

	/**
	 * Test the behaviour of getting payment methods for user with repository failure.
	 */
	@Test
	public void testGetPaymentMethodsForUserWithRepositoryFailure() {
		shouldFindPaymentMethodsForCustomerGuidWithResult(
				ExecutionResultFactory.createNotFound(PAYMENT_METHODS_NOT_FOUND));
		setupUserIdentifier(CUSTOMER_GUID);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.getPaymentMethodsForUser(STORE_CODE, CUSTOMER_GUID);
	}

	/**
	 * Tests getting a payment method with a valid profile and payment method id.
	 */
	@Test
	public void testGetPaymentMethod() {
		PaymentMethod customerPaymentMethod = new PaymentMethod() { };
		PaymentMethodEntity expectedPaymentMethod = PaymentMethodEntity.builder()
				.build();

		shouldFindPaymentMethodByCustomerGuidAndCardGuidWithResult(ExecutionResultFactory.createReadOK(customerPaymentMethod));
		shouldTransformToEntity(customerPaymentMethod, expectedPaymentMethod);
		setupUserIdentifier(CUSTOMER_GUID);

		ExecutionResult<PaymentMethodEntity> result = paymentMethodLookupStrategy.getPaymentMethod(STORE_CODE, PAYMENT_METHOD_ID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("Returned payment method dto should be the same as expected", expectedPaymentMethod, result.getData());
	}

	/**
	 * Tests getting a payment method with a valid profile and payment method id.
	 */
	@Test
	public void testGetPaymentMethodWhenPaymentMethodNotFound() {
		shouldFindPaymentMethodByCustomerGuidAndCardGuidWithResult(ExecutionResultFactory.createNotFound());
		setupUserIdentifier(CUSTOMER_GUID);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.getPaymentMethod(STORE_CODE, PAYMENT_METHOD_ID);
	}

	@Test
	public void ensureSelectorChosenAttemptsToFindOrder() {
		when(cartOrderRepository.findByGuid(STORE_CODE, ORDER_GUID)).thenReturn(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(STORE_CODE, CUSTOMER_GUID, ORDER_GUID);
		verify(cartOrderRepository, times(1)).findByGuid(STORE_CODE, ORDER_GUID);
	}

	@Test
	public void ensureSelectorChosenReturnsNotFoundWhenOrderNotFound() {
		when(cartOrderRepository.findByGuid(STORE_CODE, ORDER_GUID)).thenReturn(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(STORE_CODE, CUSTOMER_GUID, ORDER_GUID);
	}

	@Test
	public void ensureSelectorChosenAttemptsToFindCustomer() {
		final CartOrder cartOrder = createCartOrderWithPaymentMethod();
		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		when(customerPaymentMethodsRepository.findPaymentMethodsByCustomerGuid(CUSTOMER_GUID))
				.thenReturn(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(STORE_CODE, CUSTOMER_GUID, ORDER_GUID);
		verify(customerPaymentMethodsRepository, times(1)).findPaymentMethodsByCustomerGuid(CUSTOMER_GUID);
	}

	@Test
	public void ensureSelectorChosenReturnsNotFoundWhenCustomerPaymentMethodsNotFound() {
		final CartOrder cartOrder = createCartOrderWithPaymentMethod();
		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		when(customerPaymentMethodsRepository.findPaymentMethodsByCustomerGuid(CUSTOMER_GUID))
				.thenReturn(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(STORE_CODE, CUSTOMER_GUID, ORDER_GUID);
	}

	@Test
	public void ensureSelectorChosenAttemptsToResolvePaymentMethod() {
		final CartOrder cartOrder = createCartOrderWithPaymentMethod();
		PaymentMethod paymentMethod = cartOrder.getPaymentMethod();
		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));

		when(customerPaymentMethodsRepository.findPaymentMethodsByCustomerGuid(CUSTOMER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(customerPaymentMethods));
		when(customerPaymentMethods.resolve(same(paymentMethod))).thenAnswer(RETURNS_DEFAULTS);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(STORE_CODE, CUSTOMER_GUID, ORDER_GUID);

		verify(customerPaymentMethods, times(1)).resolve(same(paymentMethod));
	}

	@Test
	public void ensureSelectorChosenReturnsNotFoundWhenOrderHasNoPaymentMethod() {
		final CartOrder cartOrder = createCartOrderWithoutPaymentMethod();
		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(STORE_CODE, CUSTOMER_GUID, ORDER_GUID);
		verify(customerPaymentMethods, never()).resolve(isNull(PaymentMethod.class));
	}

	@Test
	public void ensureSelectorChosenReturnsNotFoundWhenPaymentMethodCannotBeResolved() {
		final CartOrder cartOrder = createCartOrderWithPaymentMethod();
		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));

		when(customerPaymentMethodsRepository.findPaymentMethodsByCustomerGuid(CUSTOMER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(customerPaymentMethods));
		when(customerPaymentMethods.resolve(any(PaymentMethod.class))).thenReturn(null);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(STORE_CODE, CUSTOMER_GUID, ORDER_GUID);
	}

	@Test
	public void ensureSelectorChosenAttemptsToTransformResolvedPaymentMethod() {
		final CartOrder cartOrder = createCartOrderWithPaymentMethod();
		PaymentMethod paymentMethod = new PaymentMethod() { };
		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		when(customerPaymentMethods.resolve(any(PaymentMethod.class))).thenReturn(paymentMethod);
		when(customerPaymentMethodsRepository.findPaymentMethodsByCustomerGuid(CUSTOMER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(customerPaymentMethods));
		paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(STORE_CODE, CUSTOMER_GUID, ORDER_GUID);

		verify(paymentMethodTransformer, times(1)).transformToEntity(same(paymentMethod));
	}

	@Test
	public void ensureSelectorChosenReturnsTransformedPaymentMethod() {
		final CartOrder cartOrder = createCartOrderWithPaymentMethod();
		final PaymentMethodEntity paymentMethodEntity = PaymentMethodEntity.builder()
				.withPaymentMethodId("specific-correlation-id")
				.build();

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		when(customerPaymentMethods.resolve(any(PaymentMethod.class))).thenAnswer(RETURNS_SMART_NULLS);
		when(customerPaymentMethodsRepository.findPaymentMethodsByCustomerGuid(CUSTOMER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(customerPaymentMethods));
		when(paymentMethodTransformer.transformToEntity(any(PaymentMethod.class))).thenReturn(paymentMethodEntity);

		ExecutionResult<PaymentMethodEntity> result =
				paymentMethodLookupStrategy.getSelectorChosenPaymentMethod(STORE_CODE, CUSTOMER_GUID, ORDER_GUID);
		assertExecutionResult(result)
				.isSuccessful()
				.data(paymentMethodEntity);
	}

	private void setupUserIdentifier(final String customerGuid) {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(customerGuid);
	}

	private void shouldFindByGuidWithResult(final ExecutionResult<CartOrder> result) {
		when(cartOrderRepository.findByGuid(STORE_CODE, ORDER_GUID)).thenReturn(result);
	}

	private void shouldCalculateTotal(final ExecutionResult<Money> result) {
		when(totalsCalculator.calculateSubTotalForCartOrder(STORE_CODE, ORDER_GUID)).thenReturn(result);
	}

	private void shouldFindPaymentMethodByCustomerGuidAndCardGuidWithResult(final ExecutionResult<PaymentMethod> result) {
		when(customerPaymentMethodsRepository.findPaymentMethodByCustomerGuidAndPaymentMethodId(CUSTOMER_GUID, PAYMENT_METHOD_ID)).thenReturn(result);
	}

	private void shouldFindPaymentMethodsForCustomerGuidWithResult(final ExecutionResult<CustomerPaymentMethods> result) {
		when(customerPaymentMethodsRepository.findPaymentMethodsByCustomerGuid(CUSTOMER_GUID)).thenReturn(result);
	}

	private void shouldTransformToEntity(final PaymentMethod paymentMethod, final PaymentMethodEntity result) {
		when(paymentMethodTransformer.transformToEntity(paymentMethod)).thenReturn(result);
	}

	private CartOrder createCartOrderWithPaymentMethod() {
		when(cartOrder.getPaymentMethod()).thenReturn(abstractPaymentMethod);
		return cartOrder;
	}
	private CartOrder createCartOrderWithoutPaymentMethod() {
		when(cartOrder.getPaymentMethod()).thenReturn(null);
		return cartOrder;
	}
}
