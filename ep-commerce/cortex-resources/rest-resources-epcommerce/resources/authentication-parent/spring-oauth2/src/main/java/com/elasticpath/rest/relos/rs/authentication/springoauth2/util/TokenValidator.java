/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.util;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;

/**
 * Token Validator Util.
 */
public interface TokenValidator {

	/**
	 * Validates the token.
	 *
	 * @param tokenValue the token value
	 * @return Access Token Dto.
	 */
	ExecutionResult<AccessTokenDto> validateToken(String tokenValue);
}
