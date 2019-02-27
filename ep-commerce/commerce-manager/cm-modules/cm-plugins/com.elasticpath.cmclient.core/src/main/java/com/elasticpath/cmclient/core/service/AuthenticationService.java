/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.service;

/**
 * Provides user authentication services to the RCP CM Client.
 */
public interface AuthenticationService {

	/**
	 * Determine if the user has been authenticated.
	 * 
	 * @return true if authenticated, false if not
	 */
	boolean isAuthenticated();

	/**
	 * Attempt to authenticate a user against a remote server.
	 * 
	 * @param username the userId
	 * @param password the password
	 */
	void login(String username, String password);

}
