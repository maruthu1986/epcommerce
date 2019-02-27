/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.exception;

/**
 * This exception identifies problems with tax calculation service.
 */
public class TaxCalculationServiceException extends TaxCalculationException {
	
	
	/** Serial version id. */
	private static final long serialVersionUID = 7000000001L;
	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 */
	public TaxCalculationServiceException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 * @param cause the cause of the exception
	 */
	public TaxCalculationServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
