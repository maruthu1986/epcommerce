/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Test class for {@link PaymentMethodWriterStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodWriterStrategyImplTest {
	private static final String DECODED_PAYMENT_METHOD_ID = "12345";
	private static final String SCOPE = "scope";
	private static final String DECODED_ORDER_ID = "orderId";
	private static final String DECODED_PROFILE_ID = "decodedProfileId";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private CustomerPaymentMethods customerPaymentMethods;
	@Mock
	private Customer customer;

	@InjectMocks
	private PaymentMethodWriterStrategyImpl paymentMethodWriterStrategy;
	private CartOrder cartOrder;
	private PaymentMethod paymentMethod;

	@Before
	public void setUpHappyCollaborators() {
		cartOrder = new CartOrderImpl();
		paymentMethod = new PaymentTokenImpl.TokenBuilder().build();
		((PaymentToken) paymentMethod).setUidPk(Long.valueOf(DECODED_PAYMENT_METHOD_ID));

		when(customer.getPaymentMethods()).thenReturn(customerPaymentMethods);
		when(customerPaymentMethods.getByUidPk(Long.valueOf(DECODED_PAYMENT_METHOD_ID))).thenReturn(paymentMethod);
		when(customerPaymentMethods.remove(paymentMethod)).thenReturn(true);

		when(resourceOperationContext.getUserIdentifier()).thenReturn(DECODED_PROFILE_ID);

		when(cartOrderRepository.findByGuid(SCOPE, DECODED_ORDER_ID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrderRepository.saveCartOrder(cartOrder)).thenReturn(ExecutionResultFactory.createUpdateOK());

		when(customerRepository.findCustomerByGuid(DECODED_PROFILE_ID)).thenReturn(ExecutionResultFactory.createReadOK(customer));

		when(customerRepository.updateCustomer(customer)).thenReturn(ExecutionResultFactory.createUpdateOK());
	}

	@Test
	public void verifyCustomerIsLookedUpWhenUpdatingPaymentMethodSelection() {
		paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(SCOPE, DECODED_ORDER_ID, DECODED_PAYMENT_METHOD_ID);

		verify(customerRepository, times(1)).findCustomerByGuid(DECODED_PROFILE_ID);
	}

	@Test
	public void verifyCartOrderIsLookedUpWhenUpdatingPaymentMethodSelection() {
		paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(SCOPE, DECODED_ORDER_ID, DECODED_PAYMENT_METHOD_ID);

		verify(cartOrderRepository, times(1)).findByGuid(SCOPE, DECODED_ORDER_ID);
	}

	@Test
	public void verifyCartOrderIsUpdatedWhenPaymentMethodSelectionIsUpdated() {
		paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(SCOPE, DECODED_ORDER_ID, DECODED_PAYMENT_METHOD_ID);

		verify(cartOrderRepository, times(1)).saveCartOrder(cartOrder);
	}

	@Test
	public void ensureNotFoundWhenCustomerWithCartOrderIsNotFound() {
		when(customerRepository.findCustomerByGuid(DECODED_PROFILE_ID))
				.thenReturn(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(SCOPE, DECODED_ORDER_ID, DECODED_PAYMENT_METHOD_ID);
	}

	@Test
	public void ensureCartOrderUpdatedSuccesfullyReturnsReadOk() {
		ExecutionResult<Boolean> result =
				paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(SCOPE, DECODED_ORDER_ID, DECODED_PAYMENT_METHOD_ID);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void ensureCartOrderUpdatedWithoutPreExistingPaymentMethodReturnsFalse() {
		ExecutionResult<Boolean> result =
				paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(SCOPE, DECODED_ORDER_ID, DECODED_PAYMENT_METHOD_ID);

		assertExecutionResult(result)
				.data(false);
	}

	@Test
	public void ensureCartOrderUpdatedWithPreExistingPaymentMethodReturnsTrue() {
		cartOrder.usePaymentMethod(paymentMethod);

		ExecutionResult<Boolean> result =
				paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(SCOPE, DECODED_ORDER_ID, DECODED_PAYMENT_METHOD_ID);

		assertExecutionResult(result)
				.data(true);
	}

	@Test
	public void ensureCartOrderIsUpdatedWithPaymentMethodSelection() {
		paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(SCOPE, DECODED_ORDER_ID, DECODED_PAYMENT_METHOD_ID);

		assertEquals("The cart order should be updated with newly selected payment method", paymentMethod, cartOrder.getPaymentMethod());
	}

	@Test
	public void ensureNotFoundIsReturnedWhenOrderNotFound() {
		when(cartOrderRepository.findByGuid(SCOPE, DECODED_ORDER_ID)).thenReturn(ExecutionResultFactory.<CartOrder>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(SCOPE, DECODED_ORDER_ID, DECODED_PAYMENT_METHOD_ID);

	}

	@Test
	public void ensureStateFailureIsReturnedWhenOrderUpdateFails() {
		when(cartOrderRepository.saveCartOrder(cartOrder))
				.thenReturn(ExecutionResultFactory.createStateFailure(""));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		paymentMethodWriterStrategy.updatePaymentMethodSelectionForOrder(SCOPE, DECODED_ORDER_ID, DECODED_PAYMENT_METHOD_ID);
	}

	@Test
	public void verifyCustomerIsLookedUpWhenDeletingPaymentMethod() {
		paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_PROFILE_ID, DECODED_PAYMENT_METHOD_ID);

		verify(customerRepository, times(1)).findCustomerByGuid(DECODED_PROFILE_ID);
	}

	@Test
	public void verifyCustomerIsUpdatedAfterPaymentMethodIsDeleted() {
		paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_PROFILE_ID, DECODED_PAYMENT_METHOD_ID);

		verify(customerRepository, times(1)).updateCustomer(customer);
	}

	@Test
	public void ensurePaymentMethodSuccessfullyDeletedOnProfileReturnsDeleteOk() {
		ExecutionResult<Void> result = paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_PROFILE_ID, DECODED_PAYMENT_METHOD_ID);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.DELETE_OK);
	}

	@Test
	public void ensureDeletingPaymentMethodOnProfileRemovesFromCustomerPaymentMethods() {
		paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_PROFILE_ID, DECODED_PAYMENT_METHOD_ID);

		assertFalse("The customer's payment methods should no longer contain the one removed",
				customer.getPaymentMethods().contains(paymentMethod));
	}

	@Test
	public void ensureServerErrorIsReturnedWhenPaymentMethodFailsToBeRemoved() {
		when(customerPaymentMethods.remove(paymentMethod)).thenReturn(false);
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_PROFILE_ID, DECODED_PAYMENT_METHOD_ID);
	}

	@Test
	public void ensureServerErrorIsReturnedWhenCustomerFailsToUpdate() {
		when(customerRepository.updateCustomer(customer))
				.thenReturn(ExecutionResultFactory.createServerError(""));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_PROFILE_ID, DECODED_PAYMENT_METHOD_ID);
	}

	@Test
	public void ensureNotFoundWhenCustomerWithPaymentMethodToDeleteNotFound() {
		when(customerRepository.findCustomerByGuid(DECODED_PROFILE_ID))
				.thenReturn(ExecutionResultFactory.createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_PROFILE_ID, DECODED_PAYMENT_METHOD_ID);


	}

	@Test
	public void ensureNotFoundWhenPaymentMethodToDeleteNotFound() {
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMethodWriterStrategy.deletePaymentMethodForProfile(DECODED_PROFILE_ID, "54321");
	}

}
