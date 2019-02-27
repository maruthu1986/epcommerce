/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.impl;

import org.osgi.service.component.annotations.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.UserTokenService;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.util.AuthenticationUtil;

/**
 * Service for creating a user authentication token.
 */
@Component
public class UserTokenServiceImpl implements UserTokenService {

	/**
	 * Create a user authentication token.
	 *
	 * @param username the username to use
	 * @param password the associated password
	 * @param realm the realm in which this token is valid
	 * @return an {@link Authentication} execution result
	 */
	@Override
	public ExecutionResult<Authentication> createUserAuthenticationToken(final String username, final String password, final String realm) {
		String storeCodeUsername = AuthenticationUtil.combinePrincipals(realm, username);
		Authentication userAuthentication = new UsernamePasswordAuthenticationToken(storeCodeUsername, password);
		return ExecutionResultFactory.createReadOK(userAuthentication);
	}
}
