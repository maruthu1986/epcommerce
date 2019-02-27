/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

/**
 * Login manager interface that provides methods for perform login action.
 */
public interface ILoginManager {

	/**
	 * Executes login action with given parameters. Note: if all arguments are not null then system tries to login with given parameters otherwise
	 * login dialog appears.
	 * 
	 * @return true if login was successful and false otherwise
	 */
	boolean login();

}