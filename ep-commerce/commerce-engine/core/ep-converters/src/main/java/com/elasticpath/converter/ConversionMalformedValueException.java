/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.converter;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Thrown during value to type conversion, when value is not in the right format.
 */
public class ConversionMalformedValueException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Constructor with exception message.
	 *
	 * @param message the message.
	 */
	public ConversionMalformedValueException(final String message) {
		super(message);
	}

	/**
	 * Constructor with exception message and cause.
	 *
	 * @param message the message.
	 * @param cause the cause exception.
	 */
	public ConversionMalformedValueException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
