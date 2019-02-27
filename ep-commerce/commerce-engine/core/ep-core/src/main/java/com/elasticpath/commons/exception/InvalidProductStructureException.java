/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Exception that will be thrown for products with an invalid structure.
 */
public class InvalidProductStructureException extends EpSystemException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Creates a new <code>InvalidProductStructureException</code> object with the given message.
	 *
	 * @param message the reason for this <code>InvalidProductStructureException</code>.
	 */
	public InvalidProductStructureException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>InvalidProductStructureException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>InvalidProductStructureException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>InvalidProductStructureException</code>.
	 */
	public InvalidProductStructureException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
