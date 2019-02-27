/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.exception;

/**
 * This exception identifies problems with tax calculation address validation.
 */
public class TaxCalculationAddressValidationException extends TaxCalculationException {
	
	
	/** Serial version id. */
	private static final long serialVersionUID = 7000000001L;
	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 */
	public TaxCalculationAddressValidationException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 * @param cause the cause of the exception
	 */
	public TaxCalculationAddressValidationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
