/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.security;

/**
 * Gives information about authorization state of implementation.
 */
public interface Authorizable {
	
	/**
	 * Returns <code>true</code> if the current user is authorized.
	 *
	 * @return <code>true</code> if the current user is authorized
	 */
	boolean isAuthorized();
}
