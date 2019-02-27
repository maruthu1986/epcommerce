/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2;

import com.elasticpath.rest.relos.rs.authentication.User;


/**
 * User Strategy containing common operations to lookup a {@link User}.
 */
public interface OAuthUserLookupStrategy {

	/**
	 * Load user by userId.
	 *
	 * @param scope the scope
	 * @param userId the userId
	 * @return the user details
	 */
	User loadUserByUserId(String scope, String userId);
}
