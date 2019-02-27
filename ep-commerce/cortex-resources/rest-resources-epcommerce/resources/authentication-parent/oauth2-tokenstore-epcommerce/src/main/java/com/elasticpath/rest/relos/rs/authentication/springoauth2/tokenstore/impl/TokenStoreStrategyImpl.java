/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.tokenstore.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.auth.OAuth2AccessTokenMemento;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.transformer.AccessTokenMementoTransformer;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.tokenstore.TokenStoreStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.oauth2.OAuth2AccessTokenRepository;

/**
 * Token store strategy implementation.
 */
@Component
public class TokenStoreStrategyImpl implements TokenStoreStrategy {

	private static final String INVALID_TOKEN = "Invalid access token.";

	@Reference
	private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

	@Reference
	private AccessTokenMementoTransformer accessTokenMementoTransformer;


	@Override
	public ExecutionResult<Void> storeToken(final AccessTokenDto accessTokenDto) {
		OAuth2AccessTokenMemento accessTokenMemento =
				accessTokenMementoTransformer.transformToOAuth2AccessTokenMemento(accessTokenDto);
		return oAuth2AccessTokenRepository.save(accessTokenMemento);
	}

	@Override
	public ExecutionResult<AccessTokenDto> readAccessToken(final String tokenValue) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				OAuth2AccessTokenMemento accessTokenMemento = Assign.ifSuccessful(
						oAuth2AccessTokenRepository.load(tokenValue),
						OnFailure.returnNotFound(INVALID_TOKEN)
				);
				AccessTokenDto accessTokenDto = accessTokenMementoTransformer.transformToAccessTokenDto(accessTokenMemento);
				return ExecutionResultFactory.createReadOK(accessTokenDto);
			}
		}.execute();
	}

	@Override
	public ExecutionResult<Void> removeAccessToken(final String tokenValue) {
		return oAuth2AccessTokenRepository.remove(tokenValue);
	}
}
