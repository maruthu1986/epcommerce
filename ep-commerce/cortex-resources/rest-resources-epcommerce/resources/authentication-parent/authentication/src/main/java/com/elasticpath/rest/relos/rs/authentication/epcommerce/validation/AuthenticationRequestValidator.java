/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.validation;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Validator for an authentication request.
 */
public interface AuthenticationRequestValidator {

	/**
	 * Validate the authentication request for an existing user.
	 *
	 * @param storeCode the store code
	 * @param username the user name
	 * @param password the password
	 * @param role the role
	 * @return the execution result
	 */
	ExecutionResult<Void> validateRegisteredUserRequest(
			String storeCode,
			String username,
			String password,
			String role);

	/**
	 * Validate the authentication request for an anonymous user.
	 *
	 * @param storeCode the store code
	 * @param username the user name
	 * @param password the password
	 * @param role the role
	 * @return the execution result
	 */
	ExecutionResult<Void> validateAnonymousUserRequest(
			String storeCode,
			String username,
			String password,
			String role);
}
