/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
/*
* Copyright © 2013 Elastic Path Software Inc. All rights reserved.
*/
package com.elasticpath.rest.relos.rs.authentication.springoauth2.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.AuthHeaderConstants;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.OAuthAccessTokenService;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.security.TokenExpiryResolver;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.tokenstore.TokenStoreStrategy;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.transform.AccessTokenDtoTransformer;

/**
 * Service for managing Oauth Access Tokens.
 */
@Component
public class OAuthAccessTokenServiceImpl implements OAuthAccessTokenService {

	@Reference
	private TokenStoreStrategy tokenStoreStrategy;

	@Reference
	private AccessTokenDtoTransformer accessTokenDtoTransformer;

	@Reference
	private TokenExpiryResolver tokenExpiryResolver;


	@Override
	public ExecutionResult<OAuth2AccessToken> createOAuth2Token(final String userId, final String scope, final Iterable<String> roles) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Date expiryDate = Assign.ifSuccessful(tokenExpiryResolver.getTokenExpiryDate());
				AccessTokenDto accessTokenDto = ResourceTypeFactory.createResourceEntity(AccessTokenDto.class)
						.setTokenId(UUID.randomUUID().toString())
						.setTokenType(AuthHeaderConstants.AUTH_HEADER_PREFIX)
						.setExpiryDate(expiryDate)
						.setUserId(userId)
						.setScope(scope)
						.setRoles(roles);

				Iterator<String> rolesIterator = roles.iterator();
				if (rolesIterator.hasNext()) {
					accessTokenDto.setRole(rolesIterator.next());
				}
				Ensure.successful(tokenStoreStrategy.storeToken(accessTokenDto));
				OAuth2AccessToken oauth2AccessToken = accessTokenDtoTransformer.transformToOauth2AccessToken(accessTokenDto);
				return ExecutionResultFactory.createCreateOKWithData(oauth2AccessToken, false);
			}
		}.execute();
	}

	/**
	 * Removes the token.
	 *
	 * @param tokenValue the token string value
	 * @return the execution result
	 */
	public ExecutionResult<Void> removeToken(final String tokenValue) {
		return tokenStoreStrategy.removeAccessToken(tokenValue);
	}

}
