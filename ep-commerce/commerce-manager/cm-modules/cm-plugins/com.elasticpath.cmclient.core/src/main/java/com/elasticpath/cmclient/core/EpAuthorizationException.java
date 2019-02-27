/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Exception to be thrown when an attempt is made to perform an
 * action in the CM Client by a user who has insufficient permissions.
 */
public class EpAuthorizationException extends EpSystemException {

	/**
	 * Basic constructor.
	 * @param message the exception message
	 */
	public EpAuthorizationException(final String message) {
		super(message);
	}
}
