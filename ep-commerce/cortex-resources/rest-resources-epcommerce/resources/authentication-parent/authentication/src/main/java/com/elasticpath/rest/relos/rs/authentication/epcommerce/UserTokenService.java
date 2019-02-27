/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce;

import org.springframework.security.core.Authentication;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service for creating a user authentication token.
 */
public interface UserTokenService {

	/**
	 * Create a user authentication token.
	 *
	 * @param username the username to use
	 * @param password the associated password
	 * @param realm the realm in which this token is valid
	 * @return an {@link Authentication} execution result
	 */
	ExecutionResult<Authentication> createUserAuthenticationToken(String username, String password, String realm);
}
