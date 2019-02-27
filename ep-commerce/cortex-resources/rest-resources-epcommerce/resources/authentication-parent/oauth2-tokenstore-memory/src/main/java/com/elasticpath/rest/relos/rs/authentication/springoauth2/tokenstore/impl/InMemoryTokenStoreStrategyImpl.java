/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.tokenstore.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.tokenstore.TokenStoreStrategy;

/**
 * An in memory token store strategy implementation. Usually not present in a deployment but is useful for testing.
 */
@Component(property = Constants.SERVICE_RANKING + ":Integer=100")
public class InMemoryTokenStoreStrategyImpl implements TokenStoreStrategy {

	private final Map<String, AccessTokenDto> accessTokens = new ConcurrentHashMap<>();

	@Override
	public ExecutionResult<Void> storeToken(final AccessTokenDto accessTokenDto) {
		if (accessTokenDto == null || accessTokenDto.getTokenId() == null) {
			return ExecutionResultFactory.createBadRequestBody("Invalid accessTokenDto.");
		}
		accessTokens.put(accessTokenDto.getTokenId(), accessTokenDto);
		return ExecutionResultFactory.createCreateOKWithData(null, false);
	}

	@Override
	public ExecutionResult<AccessTokenDto> readAccessToken(final String tokenValue) {
		if (tokenValue == null) {
			return ExecutionResultFactory.createNotFound("Invalid token.");
		}
		AccessTokenDto accessTokenDto = accessTokens.get(tokenValue);
		if (accessTokenDto == null) {
			return ExecutionResultFactory.createNotFound("Invalid token.");
		}

		return ExecutionResultFactory.createReadOK(accessTokenDto);
	}

	@Override
	public ExecutionResult<Void> removeAccessToken(final String tokenValue) {
		if (tokenValue == null) {
			return ExecutionResultFactory.createNotFound("Invalid token.");
		}
		accessTokens.remove(tokenValue);
		return ExecutionResultFactory.createDeleteOK();
	}
}
