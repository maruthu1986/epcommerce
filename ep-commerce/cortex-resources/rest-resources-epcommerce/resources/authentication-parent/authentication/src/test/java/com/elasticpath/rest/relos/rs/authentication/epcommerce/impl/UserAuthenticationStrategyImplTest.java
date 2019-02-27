/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.TestRoleConstants;
import com.elasticpath.rest.relos.rs.authentication.User;
import com.elasticpath.rest.relos.rs.authentication.dto.AuthenticationResponseDto;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.UserTokenService;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.AuthoritiesTransformer;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.validation.AuthenticationRequestValidator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Test class for {@link UserAuthenticationStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserAuthenticationStrategyImplTest {

	private static final String SCOPE = "SCOPE";
	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String ROLE = "ROLE";
	private static final String USER_ID = "PROFILE_ID";

	@Mock
	private UserTokenService userTokenService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private AuthoritiesTransformer authoritiesTransformer;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private AuthenticationRequestValidator authenticationRequestValidator;

	@InjectMocks
	private UserAuthenticationStrategyImpl userAuthenticationStrategy;

	@Mock
	private Collection<? extends GrantedAuthority> authorities;

	/**
	 * Test the authentication flow for an anonymous user.
	 */
	@Test
	public void testAuthenticateAnonymousUser() {
		Customer customer = createCustomer();
		shouldValidateAnonymousUserRequest(SCOPE, StringUtils.EMPTY, StringUtils.EMPTY,
				TestRoleConstants.PUBLIC, ExecutionResultFactory.<Void>createUpdateOK());

		shouldGetCustomerBean(customer);
		shouldAddCustomer(customer);

		ExecutionResult<AuthenticationResponseDto> result =
				userAuthenticationStrategy.authenticate(SCOPE, StringUtils.EMPTY, StringUtils.EMPTY, TestRoleConstants.PUBLIC);

		assertEquals(USER_ID, result.getData().getId());
		assertEquals(SCOPE, result.getData().getScope());
		assertThat(result.getData().getRoles(), Matchers.contains(TestRoleConstants.PUBLIC));
	}

	/**
	 * Test the authentication flow for an existing user.
	 */
	@Test
	public void testAuthenticateExistingUser() {
		Authentication userAuthToken = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);

		User user = createUser();
		Authentication userAuthentication = createUserAuthentication(user, authorities);

		shouldValidateRegisteredUserRequest(SCOPE, USERNAME, PASSWORD, ROLE, ExecutionResultFactory.<Void>createUpdateOK());
		shouldCreateAuthenticationToken(USERNAME, PASSWORD, SCOPE, ExecutionResultFactory.createReadOK(userAuthToken));
		shouldAuthenticate(userAuthToken, userAuthentication);
		shouldTransformAuthoritiesToRole(authorities);

		ExecutionResult<AuthenticationResponseDto> result = userAuthenticationStrategy.authenticate(SCOPE, USERNAME, PASSWORD, ROLE);

		assertEquals(USER_ID, result.getData().getId());
		assertEquals(SCOPE, result.getData().getScope());
		assertThat(result.getData().getRoles(), Matchers.contains(ROLE));
	}

	private void shouldGetCustomerBean(final Customer customer) {
		when(customerRepository.createNewCustomerEntity()).thenReturn(customer);
	}

	private void shouldAddCustomer(final Customer customer) {

		when(customerRepository.addUnauthenticatedUser(customer)).thenReturn(ExecutionResultFactory.createReadOK(customer));
	}

	private void shouldCreateAuthenticationToken(final String username, final String password,
			final String scope, final ExecutionResult<Authentication> result) {
		when(userTokenService.createUserAuthenticationToken(username, password, scope)).thenReturn(result);
	}

	private void shouldAuthenticate(final Authentication authentication, final Authentication mockAuthentication) {
		when(authenticationManager.authenticate(authentication)).thenReturn(mockAuthentication);
	}

	private void shouldTransformAuthoritiesToRole(final Collection<? extends GrantedAuthority> authorities) {
		when(authoritiesTransformer.transform(authorities)).thenReturn(Collections.singleton(ROLE));
	}

	private void shouldValidateRegisteredUserRequest(final String storeCode,
			final String username,
			final String password,
			final String role,
			final ExecutionResult<Void> result) {

		when(authenticationRequestValidator.validateRegisteredUserRequest(storeCode, username, password, role)).thenReturn(result);
	}

	private void shouldValidateAnonymousUserRequest(final String storeCode,
			final String username,
			final String password,
			final String role,
			final ExecutionResult<Void> result) {

		when(authenticationRequestValidator.validateAnonymousUserRequest(storeCode, username, password, role)).thenReturn(result);
	}

	private Customer createCustomer() {
		Customer customer = mock(Customer.class);
		Mockito.doNothing().when(customer).setAnonymous(true);
		when(customer.getUserId()).thenReturn(null);
		when(customer.getGuid()).thenReturn(USER_ID);
		when(customer.getStoreCode()).thenReturn(SCOPE);

		return customer;
	}

	private Authentication createUserAuthentication(final User user, final Collection<? extends GrantedAuthority> authorities) {
		Authentication userAuthentication = mock(Authentication.class);

		when(userAuthentication.getPrincipal()).thenReturn(user);
		Mockito.doReturn(authorities).when(userAuthentication).getAuthorities();

		return userAuthentication;
	}

	private User createUser() {
		User user = mock(User.class);
		when(user.getUserId()).thenReturn(USER_ID);
		when(user.getRequestedScope()).thenReturn(SCOPE);
		return user;
	}
}
