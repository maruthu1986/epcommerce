/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
/*
* Copyright © 2013 Elastic Path Software Inc. All rights reserved.
*/
package com.elasticpath.rest.relos.rs.authentication.springoauth2;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service for managing Oauth Access Tokens.
 */
public interface OAuthAccessTokenService {

	/**
	 * Creates and store an Oauth access token.
	 *
	 * @param userId the user id
	 * @param scope the scope
	 * @param roles the roles
	 * @return the access token
	 */
	ExecutionResult<OAuth2AccessToken> createOAuth2Token(String userId, String scope, Iterable<String> roles);

	/**
	 * Removes the token.
	 *
	 * @param tokenValue the token string value
	 * @return the execution result
	 */
	ExecutionResult<Void> removeToken(String tokenValue);
}
