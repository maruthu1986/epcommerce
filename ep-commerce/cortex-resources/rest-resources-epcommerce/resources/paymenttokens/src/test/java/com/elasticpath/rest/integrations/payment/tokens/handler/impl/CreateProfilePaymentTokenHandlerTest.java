/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
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

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.integrations.payment.tokens.handler.CartOrdersDefaultPaymentMethodPopulator;
import com.elasticpath.rest.integrations.payment.tokens.transformer.PaymentTokenTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;

/**
 * Tests the {@link CreateProfilePaymentTokenHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateProfilePaymentTokenHandlerTest {
	public static final String CUSTOMER_GUID = "customerGuid";

	private static final String SCOPE = "SCOPE";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private PaymentTokenTransformer paymentTokenTransformer;
	@Mock
	private PaymentToken paymentToken;
	@Mock
	private PaymentToken updatedPaymentToken;
	@Mock
	private PaymentTokenEntity postedPaymentTokenEntity;
	@Mock
	private CartOrdersDefaultPaymentMethodPopulator cartOrdersDefaultPaymentMethodPopulator;

	@InjectMocks
	private CreateProfilePaymentTokenHandler createProfilePaymentTokenHandler;
	@Mock
	private Customer customer;
	@Mock
	private CustomerPaymentMethods customerPaymentMethods;

	@Before
	public void setupHappyCollaborators() {
		when(customer.getPaymentMethods()).thenReturn(customerPaymentMethods);
		when(customerPaymentMethods.resolve(paymentToken)).thenReturn(updatedPaymentToken);

		postedPaymentTokenEntity = PaymentTokenEntity.builder()
				.build();
		when(customerRepository.findCustomerByGuid(CUSTOMER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(customer));
		when(customerRepository.updateCustomer(customer)).thenReturn(ExecutionResultFactory.<Void>createUpdateOK());
		when(paymentTokenTransformer.transformToEntity(updatedPaymentToken)).thenReturn(postedPaymentTokenEntity);
		when(customerPaymentMethods.getDefault()).thenReturn(null);
	}

	@Test
	public void ensureCartOrdersArePopulatedWithNewDefaultPaymentMethod() {
		createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);

		verify(cartOrdersDefaultPaymentMethodPopulator, times(1)).updateAllCartOrdersPaymentMethods(customer, updatedPaymentToken, SCOPE);
	}

	@Test
	public void ensureCartOrdersArePopulatedWithUpdatedDefaultPaymentMethod() {
		when(customerPaymentMethods.getDefault()).thenReturn(mock(PaymentToken.class));
		createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);

		verify(cartOrdersDefaultPaymentMethodPopulator, times(1)).updateAllCartOrdersPaymentMethods(customer, updatedPaymentToken, SCOPE);
	}

	@Test
	public void ensureCartOrdersAreNotPopulatedWithNonDefaultPaymentMethod() {
		when(customerPaymentMethods.getDefault()).thenReturn(updatedPaymentToken);

		createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);

		verify(cartOrdersDefaultPaymentMethodPopulator, times(0)).updateAllCartOrdersPaymentMethods(customer, updatedPaymentToken, SCOPE);
	}

	@Test
	public void ensureProfileOwnerTypeIsHandled() {
		assertEquals("Profile owner types should be handled", PaymentTokenOwnerType.PROFILE_TYPE,
				createProfilePaymentTokenHandler.getHandledOwnerType());
	}

	@Test
	public void verifyCustomerIsLookedUp() {
		createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);

		verify(customerRepository, times(2)).findCustomerByGuid(CUSTOMER_GUID);
	}

	@Test
	public void verifyCustomerIsSavedWithUpdatedPaymentMethods() {
		createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);

		verify(customerRepository, times(1)).updateCustomer(customer);
	}

	@Test
	public void verifyPaymentTokenIsTransformed() {
		createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);

		verify(paymentTokenTransformer, times(1)).transformToEntity(updatedPaymentToken);
	}

	@Test
	public void ensureValidPaymentTokenAssociatedWithCustomerReturnsCreateOk() {
		ExecutionResult<PaymentTokenEntity> result = createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);

		assertExecutionResult(result)
				.resourceStatus(ResourceStatus.CREATE_OK);
	}

	@Test
	public void ensureValidPaymentTokenCanBeAssociatedWithCustomer() {
		ExecutionResult<PaymentTokenEntity> result = createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);

		assertExecutionResult(result)
				.data(postedPaymentTokenEntity);
	}

	@Test
	public void ensureNotFoundWhenCustomerDoesNotExist() {
		when(customerRepository.findCustomerByGuid(CUSTOMER_GUID)).thenReturn(ExecutionResultFactory.<Customer>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);
	}

	@Test
	public void ensureServerErrorWhenPaymentTokenFailsToBeAssociatedWithCustomer() {
		when(customerRepository.updateCustomer(customer)).thenReturn(ExecutionResultFactory.<Void>createServerError(""));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);
	}

	@Test
	public void ensureStateFailureWhenErrorOccursAssociatingPaymentTokenWithCustomer() {
		when(customerPaymentMethods.resolve(paymentToken))
				.thenReturn(null);
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		createProfilePaymentTokenHandler.createPaymentTokenForOwner(paymentToken, CUSTOMER_GUID, SCOPE);
	}
}
