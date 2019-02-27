/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.transform;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;

/**
 * Transformer class for AccessTokenDto.
 */
public interface AccessTokenDtoTransformer {

	/**
	 * Transforms {@link AccessTokenDto} to {@link OAuth2AccessToken}.
	 *
	 * @param accessTokenDto access token dto.
	 * @return the Oauth2AccessToken
	 */
	OAuth2AccessToken transformToOauth2AccessToken(AccessTokenDto accessTokenDto);
}
