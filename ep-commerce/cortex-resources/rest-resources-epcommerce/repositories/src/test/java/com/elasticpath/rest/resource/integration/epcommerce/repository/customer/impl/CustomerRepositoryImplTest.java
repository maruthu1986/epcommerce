/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Provider;

import com.google.common.collect.ImmutableMap;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * Test class for {@link CustomerRepositoryImpl}.
 */
@SuppressWarnings("PMD.TooManyMethods")
@RunWith(MockitoJUnitRunner.class)
public class CustomerRepositoryImplTest {

	private static final String THE_RESULT_SHOULD_BE_SUCCESSFUL = "The result should be successful";
	private static final String THE_RESULT_SHOULD_BE_A_FAILURE = "The result should be a failure";
	private static final String USER_ID = "userId";
	private static final String SCOPE = "scope";
	private static final String CUSTOMER_GUID = "customer_guid";
	private static final String EP_PERSISTENCE_EXCEPTION = "ep persistence exception";
	private static final String ADDRESS_GUID = "addressGuid";
	private static final String CART_GUID = "cartGuid";
	@Mock
	private CustomerService mockCustomerService;
	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;
	@Mock
	private Customer mockCustomer;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private CustomerSession mockCustomerSession;
	@Mock
	private CustomerSessionRepository mockCustomerSessionRepository;
	@Mock
	private ShoppingCartService mockShoppingCartService;
	@Mock
	private ShoppingCart mockShoppingCart;
	@Mock
	private CustomerSessionService mockCustomerSessionService;
	@Mock
	private Shopper mockShopper;
	@Mock
	private CustomerAddress mockAddress;
	@Mock
	private ExceptionTransformer exceptionTransformer;
	@Mock
	private BeanFactory coreBeanFactory;
	@Mock
	private Provider<CartOrderRepository> cartOrderRepositoryProvider;
	@Mock
	private CartOrderRepository cartOrderRepository;

	private CustomerRepositoryImpl customerRepository;

	@Before
	public void setUp() {
		customerRepository = new CustomerRepositoryImpl(mockCustomerService, mockCustomerSessionRepository, mockCustomerSessionService,
				mockShoppingCartService, coreBeanFactory, cartOrderRepositoryProvider, exceptionTransformer, reactiveAdapter);
	}

	@Test
	public void testFindCustomerByUserIdWithAppropriateParameters() {
		Customer expectedCustomer = new CustomerImpl();

		when(mockCustomerSessionRepository.findCustomerSessionByUserId(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockCustomerSession));
		when(mockCustomerSession.getShopper().getCustomer()).thenReturn(expectedCustomer);

		ExecutionResult<Customer> result = customerRepository.findCustomerByUserId(SCOPE, USER_ID);

		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("The returned customer should be the same as expected", expectedCustomer, result.getData());
	}

	@Test
	public void testFindCustomerByUserIdWhenCustomerNotFound() {
		when(mockCustomerSessionRepository.findCustomerSessionByUserId(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockCustomerSession));
		when(mockCustomerSession.getShopper().getCustomer()).thenReturn(null);

		ExecutionResult<Customer> result = customerRepository.findCustomerByUserId(SCOPE, USER_ID);

		assertTrue(THE_RESULT_SHOULD_BE_A_FAILURE, result.isFailure());
		assertEquals("The status returned should be NOT_FOUND", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	@Test
	public void testFindCustomerByUserIdWhenExceptionThrown() {
		when(mockCustomerSessionRepository.findCustomerSessionByUserId(SCOPE, USER_ID))
				.thenThrow(new EpPersistenceException(EP_PERSISTENCE_EXCEPTION));

		ExecutionResult<Customer> result = customerRepository.findCustomerByUserId(SCOPE, USER_ID);
		assertServerError(result);

	}

	@Test
	public void testFindCustomerByGuidWithValidGuid() {
		Customer expectedCustomer = new CustomerImpl();

		when(mockCustomerSessionRepository.findCustomerSessionByGuid(CUSTOMER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockCustomerSession));
		when(mockCustomerSession.getShopper().getCustomer()).thenReturn(expectedCustomer);

		ExecutionResult<Customer> result = customerRepository.findCustomerByGuid(CUSTOMER_GUID);

		assertTrue("The result should be a success", result.isSuccessful());
		assertEquals("The customer returned should be the same as expected", expectedCustomer, result.getData());
	}

	@Test
	public void testFindCustomerByGuidWithCustomerNotFound() {
		when(mockCustomerSessionRepository.findCustomerSessionByGuid(CUSTOMER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockCustomerSession));
		when(mockCustomerSession.getShopper().getCustomer()).thenReturn(null);

		ExecutionResult<Customer> result = customerRepository.findCustomerByGuid(CUSTOMER_GUID);

		assertTrue("The result should be a failure", result.isFailure());
		assertEquals("The resource status should be NOT_FOUND", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	@Test
	public void testFindCustomerByGuidWhenExceptionThrown() {
		when(mockCustomerSessionRepository.findCustomerSessionByGuid(CUSTOMER_GUID))
				.thenThrow(new EpPersistenceException(EP_PERSISTENCE_EXCEPTION));

		ExecutionResult<Customer> result = customerRepository.findCustomerByGuid(CUSTOMER_GUID);
		assertServerError(result);
	}

	@Test
	public void testUpdateCustomerWithExistingUserId() {
		Customer customer = new CustomerImpl();
		String errorMessage = "debug message";
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage(
				"message-id",
				errorMessage,
				ImmutableMap.of("key", "value")
		);

		when(exceptionTransformer.getExecutionResult(any(UserIdExistException.class))).thenReturn(ExecutionResultFactory
				.createStateFailureWithMessages(errorMessage, asList(mock(Message.class))));
		when(mockCustomerService.update(customer)).thenThrow(
				new UserIdExistException(
						errorMessage,
						asList(structuredErrorMessage)

				)
		);

		ExecutionResult result = customerRepository.updateCustomer(customer);
		assertTrue(THE_RESULT_SHOULD_BE_A_FAILURE, result.isFailure());
		assertEquals("The resource status returned should be a STATE_FAILURE", ResourceStatus.STATE_FAILURE, result.getResourceStatus());
	}

	@Test
	public void testUpdateCustomerWithExceptionThrown() {
		Customer customer = new CustomerImpl();
		when(mockCustomerService.update(customer)).thenThrow(new EpPersistenceException(""));

		ExecutionResult result = customerRepository.updateCustomer(customer);
		assertServerError(result);
	}

	private void assertServerError(final ExecutionResult result) {
		assertTrue(THE_RESULT_SHOULD_BE_A_FAILURE, result.isFailure());
		assertEquals("The resource status returned should be a SERVER_ERROR", ResourceStatus.SERVER_ERROR, result.getResourceStatus());
	}

	@Test
	public void testUpdateCustomer() {
		Customer customer = new CustomerImpl();
		when(mockCustomerService.update(customer)).thenReturn(customer);

		ExecutionResult result = customerRepository.updateCustomer(customer);
		assertTrue(THE_RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("The resource status returned should be a UPDATE_OK", ResourceStatus.UPDATE_OK, result.getResourceStatus());
	}

	@Test
	public void mergeCustomerShouldReturnErrorWhenAMergeCustomerFails() throws Exception {
		when(mockShoppingCartService.findOrCreateByShopper(any(Shopper.class))).thenReturn(mockShoppingCart);
		when(mockShoppingCart.getShopper()).thenReturn(mockShopper);
		doThrow(new IllegalArgumentException()).when(mockCustomerSessionService).changeFromAnonymousToRegisteredCustomer(
				mockCustomerSession,
				mockCustomer,
				SCOPE);

		ExecutionResult<Object> executionResult = customerRepository.mergeCustomer(mockCustomerSession, mockCustomer, SCOPE);
		assertServerError(executionResult);
	}

	@Test
	public void mergeCustomerShouldReturnSuccess() throws Exception {
		when(mockShoppingCartService.findOrCreateByShopper(any(Shopper.class))).thenReturn(mockShoppingCart);
		when(mockCustomerSession.getShopper()).thenReturn(mockShopper);
		when(mockShoppingCart.getShopper()).thenReturn(mockShopper);

		ExecutionResult<Object> executionResult = customerRepository.mergeCustomer(mockCustomerSession, mockCustomer, SCOPE);

		assertEquals("The resultStatus shoud be OK", ResourceStatus.UPDATE_OK, executionResult.getResourceStatus());
	}


	@Test
	public void testAddUnauthenticatedUser() {
		when(mockCustomerService.addByAuthenticate(mockCustomer, false)).thenReturn(mockCustomer);

		customerRepository.addUnauthenticatedUser(mockCustomer);

		verify(mockCustomerService, atLeastOnce()).addByAuthenticate(mockCustomer, false);
	}

	@Test
	public void testAddAddress() throws Exception {
		when(mockCustomerService.addOrUpdateAddress(mockCustomer, mockAddress)).thenReturn(mockCustomer);

		customerRepository.addAddress(mockCustomer, mockAddress)
				.test()
				.assertNoErrors()
				.assertValue(mockCustomer);
	}

	@Test
	public void shouldUpdateShippingAddressOnCartWhenAddressExists() {
		when(mockCustomer.getPreferredShippingAddress()).thenReturn(mockAddress);
		when(mockCustomer.getStoreCode()).thenReturn(SCOPE);
		when(mockCustomer.getGuid()).thenReturn(CUSTOMER_GUID);
		when(mockAddress.getGuid()).thenReturn(ADDRESS_GUID);
		when(cartOrderRepositoryProvider.get()).thenReturn(cartOrderRepository);
		when(cartOrderRepository.findCartOrderGuidsByCustomerAsObservable(SCOPE, CUSTOMER_GUID)).thenReturn(Observable.just(CART_GUID));
		when(cartOrderRepository.updateShippingAddressOnCartOrderAsSingle(ADDRESS_GUID, CART_GUID, SCOPE))
				.thenReturn(Single.just(true));

		customerRepository.updateShippingAddressOnCustomerCart(mockCustomer, mockAddress)
				.test()
				.assertNoErrors();

		verify(cartOrderRepository, times(1)).findCartOrderGuidsByCustomerAsObservable(SCOPE, CUSTOMER_GUID);
		verify(cartOrderRepository, times(1)).updateShippingAddressOnCartOrderAsSingle(ADDRESS_GUID, CART_GUID, SCOPE);
	}


	@Test
	public void testUpdateInCache() throws Exception {
		when(mockCustomerService.update(mockCustomer)).thenReturn(mockCustomer);

		customerRepository.update(mockCustomer)
				.test()
				.assertNoErrors()
				.assertValue(mockCustomer);
	}

	@Test
	public void testIsFirstTimeBuyer() throws Exception {
		Customer customer = mock(Customer.class);
		when(customer.isFirstTimeBuyer()).thenReturn(true);

		assertTrue(customerRepository.isFirstTimeBuyer(customer));
	}

	@Test
	public void testIsNotFirstTimeBuyer() throws Exception {
		Customer customer = mock(Customer.class);
		when(customer.isFirstTimeBuyer()).thenReturn(false);

		assertFalse(customerRepository.isFirstTimeBuyer(customer));
	}
}
