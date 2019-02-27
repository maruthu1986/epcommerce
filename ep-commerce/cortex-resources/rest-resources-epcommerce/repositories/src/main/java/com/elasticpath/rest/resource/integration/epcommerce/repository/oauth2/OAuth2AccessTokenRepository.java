/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.oauth2;

import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The facade for {@link com.elasticpath.service.auth.OAuth2AccessTokenService} related operations.
 */
public interface OAuth2AccessTokenRepository {

	/**
	 * Stores the given token to token store.
	 *
	 * @param oAuth2AccessTokenMemento OAuth2 access token
	 * @return the execution result.
	 */
	ExecutionResult<Void> save(OAuth2AccessTokenMemento oAuth2AccessTokenMemento);

	/**
	 * Get the access token with the given token value.
	 *
	 * @param tokenValue the token value
	 * @return the execution result
	 */
	ExecutionResult<OAuth2AccessTokenMemento> load(String tokenValue);

	/**
	 * Remove an access token from the database.
	 *
	 * @param tokenValue the token value
	 * @return the execution result
	 */
	ExecutionResult<Void> remove(String tokenValue);

	/**
	 * Creates an instance of OAuth2AccessTokenMemento with the {@link com.elasticpath.commons.beanframework.BeanFactory}.
	 *
	 * @return the execution result
	 */
	ExecutionResult<OAuth2AccessTokenMemento> createOAuth2AccessToken();
}
