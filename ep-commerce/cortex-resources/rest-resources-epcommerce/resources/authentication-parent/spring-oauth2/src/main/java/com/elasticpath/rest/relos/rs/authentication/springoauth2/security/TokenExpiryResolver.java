/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.security;

import java.util.Date;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Token expiry resolver.
 */
public interface TokenExpiryResolver {

	/**
	 * Gets the token expiry date.
	 *
	 * @return the Date the token will expire.
	 */
	ExecutionResult<Date> getTokenExpiryDate();
}
