/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core;

/**
 * Exception indicating that a problem has occurred with attempting to retrieve a message from a Messages class. Currently it only allows the
 * wrapping of a checked exception into an unchecked one.
 */
public class MessageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param cause the wrapped exception
	 */
	public MessageException(final Throwable cause) {
		super(cause);
	}

}
