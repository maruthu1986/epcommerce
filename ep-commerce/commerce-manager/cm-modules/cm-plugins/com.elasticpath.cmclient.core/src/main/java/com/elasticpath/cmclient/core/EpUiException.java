/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core;

/**
 * Generic exception indicating that a problem has occurred in the UI.
 * This can be used to wrap checked exceptions with this unchecked one.
 */
public class EpUiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message the message 
	 * @param cause the wrapped exception
	 */
	public EpUiException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 *
	 * @param cause the wrapped exception
	 */
	public EpUiException(final Throwable cause) {
		super(cause);
	}
	
}
