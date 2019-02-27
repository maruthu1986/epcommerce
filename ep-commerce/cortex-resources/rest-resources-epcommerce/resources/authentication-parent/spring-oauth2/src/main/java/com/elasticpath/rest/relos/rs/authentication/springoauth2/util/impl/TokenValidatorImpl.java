/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.util.impl;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.tokenstore.TokenStoreStrategy;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.util.TokenValidator;

/**
 * Token Validator Util.
 */
@Component
public class TokenValidatorImpl implements TokenValidator {

	private static final String INVALID_TOKEN = "Invalid Token";

	@Reference
	private TokenStoreStrategy tokenStoreStrategy;


	@Override
	public ExecutionResult<AccessTokenDto> validateToken(final String tokenValue) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				AccessTokenDto accessTokenDto = Assign.ifSuccessful(tokenStoreStrategy.readAccessToken(tokenValue),
						OnFailure.returnBadRequestBody(INVALID_TOKEN));
				Ensure.isTrue(isTokenValid(accessTokenDto),
						OnFailure.returnBadRequestBody(INVALID_TOKEN));
				return ExecutionResultFactory.createCreateOKWithData(accessTokenDto, false);
			}
		}.execute();
	}

	private boolean isTokenValid(final AccessTokenDto accessTokenDto) {
		Date expiryDate = accessTokenDto.getExpiryDate();
		return expiryDate != null && expiryDate.after(new Date());
	}
}
