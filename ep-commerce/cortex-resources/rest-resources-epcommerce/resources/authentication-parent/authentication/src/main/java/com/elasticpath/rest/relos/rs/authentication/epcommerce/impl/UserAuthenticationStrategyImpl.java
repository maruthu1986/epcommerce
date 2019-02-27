/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.impl;

import java.util.Collection;
import java.util.Collections;

import com.elasticpath.rest.relos.rs.authentication.epcommerce.UserTokenService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.User;
import com.elasticpath.rest.relos.rs.authentication.UserAuthenticationStrategy;
import com.elasticpath.rest.relos.rs.authentication.dto.AuthenticationResponseDto;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.AuthoritiesTransformer;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.validation.AuthenticationRequestValidator;
import com.elasticpath.rest.resource.integration.epcommerce.common.authentication.AuthenticationConstants;

/**
 * User authentication.
 */
@Component
public class UserAuthenticationStrategyImpl implements UserAuthenticationStrategy {

	private static final String BAD_CREDENTIALS = "Bad credentials.";

	@Reference
	private UserTokenService userTokenService;

	@Reference
	private AuthenticationManager authenticationManager;

	@Reference
	private AuthoritiesTransformer authoritiesTransformer;

	@Reference
	private CustomerRepository customerRepository;

	@Reference
	private AuthenticationRequestValidator authenticationRequestValidator;


	@Override
	public ExecutionResult<AuthenticationResponseDto> authenticate(final String storeCode,
			final String username,
			final String password,
			final String role) {

		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				if (isAnonymousAuthenticationRequest(role)) {
					Ensure.successful(authenticationRequestValidator.validateAnonymousUserRequest(storeCode, username, password, role));
					return authenticateAsAnonymousUser(storeCode, role);
				} else {
					Ensure.successful(authenticationRequestValidator.validateRegisteredUserRequest(storeCode, username, password, role));
					return authenticateAsExistingUser(storeCode, username, password, role);
				}
			}
		}.execute();
	}

	private ExecutionResult<AuthenticationResponseDto> authenticateAsAnonymousUser(final String storeCode, final String role) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {

				Customer customer = createAnonymousCustomer(storeCode);
				Ensure.successful(customerRepository.addUnauthenticatedUser(customer),
										OnFailure.returnUnauthorized(BAD_CREDENTIALS));

				AuthenticationResponseDto dto = ResourceTypeFactory.createResourceEntity(AuthenticationResponseDto.class)
						.setId(customer.getGuid())
						.setScope(customer.getStoreCode())
						.setRoles(Collections.singleton(role));

				return ExecutionResultFactory.createReadOK(dto);
			}
		}.execute();
	}

	private ExecutionResult<AuthenticationResponseDto> authenticateAsExistingUser(final String storeCode,
			final String username,
			final String password,
			final String role) {

		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				ExecutionResult<AuthenticationResponseDto> result;

				Authentication authenticationRequest = Assign.ifSuccessful(userTokenService.createUserAuthenticationToken(
						username, password, storeCode), OnFailure.returnUnauthorized(BAD_CREDENTIALS));
				try {
					Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
					User user = (User) authenticationResult.getPrincipal();

					// the current authentication manager does not match against role, so we do it here explicitly
					Collection<String> authorities = authoritiesTransformer.transform(authenticationResult.getAuthorities());

					Ensure.isTrue(authorities.contains(role),
							OnFailure.returnUnauthorized(BAD_CREDENTIALS));
					AuthenticationResponseDto dto = ResourceTypeFactory.createResourceEntity(AuthenticationResponseDto.class)
							.setId(user.getUserId())
							.setScope(user.getRequestedScope())
							.setRoles(authorities);
					result = ExecutionResultFactory.createReadOK(dto);
				} catch (final BadCredentialsException exception) {
					result = ExecutionResultFactory.createUnauthorized(BAD_CREDENTIALS);
				} catch (final DisabledException exception) {
					result = ExecutionResultFactory.createUnauthorized("User is disabled");
				} catch (final LockedException exception) { // mimics status: pending approval
					result = ExecutionResultFactory.createUnauthorized("User is locked");
				} catch (final AuthenticationServiceException exception) {
					result = ExecutionResultFactory.createServerError(exception.getMessage());
				} catch (final AuthenticationException exception) {
					// catch all to handle potential exceptions such as CredentialsExpiredException, AccountExpiredException
					result = ExecutionResultFactory.createUnauthorized(exception.getMessage());
				}

				return result;
			}
		}.execute();
	}

	private boolean isAnonymousAuthenticationRequest(final String role) {
		return AuthenticationConstants.PUBLIC_ROLENAME.equals(role);
	}

	private Customer createAnonymousCustomer(final String storeCode) {
		Customer customer = customerRepository.createNewCustomerEntity();
		customer.setAnonymous(true);
		customer.setStoreCode(storeCode);

		/*
		 * Core bases the user Id on the email, with a value that depends upon a UserIdMode being set.
		 * This ensures that both the userId and email are set appropriately based on the supplied username.
		 */
		customer.setEmail(AuthenticationConstants.ANONYMOUS_USER_ID);
		if (customer.getUserId() == null) {
			customer.setUserId(AuthenticationConstants.ANONYMOUS_USER_ID);
		}

		return customer;
	}
}
