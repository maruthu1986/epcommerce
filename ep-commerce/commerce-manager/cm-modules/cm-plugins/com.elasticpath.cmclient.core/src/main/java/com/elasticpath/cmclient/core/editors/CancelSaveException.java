/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.editors;

/**
 * A runtime exception used to cancel the save procedure.
 */
public class CancelSaveException extends RuntimeException {
	private static final long serialVersionUID = -7825485715762636501L;

	/**
	 * Constructs the exception.
	 * @param message the exception message
	 */
	public CancelSaveException(final String message) {
		super(message);
	}

	/**
	 * Constructs the exception.
	 * @param cause throwable exception
	 */
	public CancelSaveException(final Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructs the exception.
	 * @param message the exception message
	 * @param cause throwable exception
	 */
	public CancelSaveException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
}
