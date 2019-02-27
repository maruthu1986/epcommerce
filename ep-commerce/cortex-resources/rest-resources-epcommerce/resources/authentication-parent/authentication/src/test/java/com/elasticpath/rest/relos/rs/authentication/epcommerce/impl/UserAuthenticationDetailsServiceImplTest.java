/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.RolePrincipal;
import com.elasticpath.rest.relos.rs.authentication.User;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.CustomerTransformer;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.util.AuthenticationUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;


/**
 * Test class for {@link UserAuthenticationDetailsServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserAuthenticationDetailsServiceImplTest {

	private static final String INVALID_USER_NAME = "invalidUser";
	private static final String DISABLED_USER_NAME = "DISABLED_USER_NAME";
	private static final String INVALID_STORE_CODE = "INVALID_STORE_CODE";
	private static final String USER_GUID = "00000000-0000-0000-0000-000000000000";
	private static final String DEFAULT_STORE_CODE = "SNAPITUP";
	private static final String VALID_USER_NAME = "validUsername";
	private static final String VALID_PASSWORD = "validPassword";

	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private CustomerTransformer mockCustomerTransformer;
	@Mock
	private Customer customer;

	@InjectMocks
	private UserAuthenticationDetailsServiceImpl userAuthenticationDetailsService;


	/**
	 * Test loadUserByUsername() with valid user.
	 */
	@Test
	public void testLoadByUserName() {
		User expectedUser = createValidUser();

		shouldFindCustomerByUserId(DEFAULT_STORE_CODE, VALID_USER_NAME, ExecutionResultFactory.createReadOK(customer));
		shouldEnableCustomer(true, customer);
		shouldTransformToUser(expectedUser, customer);

		String storePlusUser = AuthenticationUtil.combinePrincipals(DEFAULT_STORE_CODE, VALID_USER_NAME);
		UserDetails userDetails = userAuthenticationDetailsService.loadUserByUsername(storePlusUser);
		assertEquals(VALID_USER_NAME, userDetails.getUsername());
	}

	/**
	 * Test loadUserByUsername() with invalid user.
	 */
	@Test(expected = UsernameNotFoundException.class)
	public void testLoadWithInvalidStoreCode() {
		when(customerRepository.findCustomerByUserId(INVALID_STORE_CODE, INVALID_STORE_CODE))
				.thenReturn(ExecutionResultFactory.<Customer>createNotFound());
		userAuthenticationDetailsService.loadUserByUsername(INVALID_STORE_CODE, INVALID_STORE_CODE);
	}

	/**
	 * Test loadUserByUsername() with invalid user.
	 */
	@Test(expected = UsernameNotFoundException.class)
	public void testLoadInvalidUser() {
		shouldFindCustomerByUserId(DEFAULT_STORE_CODE, INVALID_USER_NAME, ExecutionResultFactory.<Customer>createNotFound());

		String storePlusUser = AuthenticationUtil.combinePrincipals(DEFAULT_STORE_CODE, INVALID_USER_NAME);
		userAuthenticationDetailsService.loadUserByUsername(storePlusUser);
	}

	/**
	 * Test loadUserByUsername() with disabled user.
	 */
	@Test(expected = UsernameNotFoundException.class)
	public void testLoadDisabledUser() {
		shouldFindCustomerByUserId(DEFAULT_STORE_CODE, DISABLED_USER_NAME, ExecutionResultFactory.createReadOK(customer));
		shouldEnableCustomer(false, customer);

		String storePlusUser = AuthenticationUtil.combinePrincipals(DEFAULT_STORE_CODE, DISABLED_USER_NAME);
		userAuthenticationDetailsService.loadUserByUsername(storePlusUser);
	}


	private void shouldTransformToUser(final User expectedUser, final Customer customer) {
		when(mockCustomerTransformer.transform(customer)).thenReturn(expectedUser);
	}

	private void shouldFindCustomerByUserId(final String storeCode, final String username, final ExecutionResult<Customer> expectedResult) {
		when(customerRepository.findCustomerByUserId(storeCode, username)).thenReturn(expectedResult);
	}

	private void shouldEnableCustomer(final boolean enabled, final Customer customer) {
		when(customer.isEnabled()).thenReturn(enabled);
	}

	private User createValidUser() {
		Collection<RolePrincipal> principals = new ArrayList<>();
		User user = new UserImpl();
		user.setUsername(VALID_USER_NAME)
				.setPassword(VALID_PASSWORD)
				.setPrincipals(principals)
				.setUserId(USER_GUID)
				.setAccountEnabled(true)
				.setAccountExpired(false)
				.setAccountLocked(false);
		return user;
	}
}
