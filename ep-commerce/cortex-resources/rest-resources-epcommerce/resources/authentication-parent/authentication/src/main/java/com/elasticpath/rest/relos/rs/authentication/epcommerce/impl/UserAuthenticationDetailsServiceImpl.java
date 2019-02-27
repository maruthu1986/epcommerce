/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.relos.rs.authentication.User;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.CustomerTransformer;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.util.AuthenticationUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Loads a user's details based on their username and scope.
 * We must implement spring security's {@link UserDetailsService} so the object can be used by spring security.
 */
@Component
public class UserAuthenticationDetailsServiceImpl implements UserDetailsService {

	@Reference
	private CustomerRepository customerRepository;

	@Reference
	private CustomerTransformer customerTransformer;


	@Override
	public UserDetails loadUserByUsername(final String principals) {
		String[] parts = AuthenticationUtil.splitPrincipals(principals);

		String storeCode = parts[0];
		String username = parts[1];
		return (UserDetails) loadUserByUsername(storeCode, username);
	}

	/**
	 * Loads user by Username.
	 *
	 * @param scope the users scope
	 * @param username the users username
	 * @return the User
	 */
	User loadUserByUsername(final String scope, final String username) {

		ExecutionResult<Customer> customerLookupResult = customerRepository.findCustomerByUserId(scope, username);

		if (customerLookupResult.isSuccessful()) {
			Customer customer = customerLookupResult.getData();
			if (isCustomerDisabled(customer)) {
				throw new UsernameNotFoundException(String.format("User with username %s is disabled", username));
			}

			return customerTransformer.transform(customer);
		}
		throw new UsernameNotFoundException(String.format("User with username %s does not exist", username));
	}

	private boolean isCustomerDisabled(final Customer customer) {
		return !customer.isEnabled();
	}
}

