/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.exception;

/**
 * The generic exception class for tax calculations.
 */
public class TaxCalculationException extends RuntimeException {

	/** Serial version id. */
	private static final long serialVersionUID = 7000000001L;
	
	/**
	 * Creates a new {@code TaxCalculationException} object with the given message.
	 * 
	 * @param message the reason for this {@code TaxCalculationException}.
	 */
	public TaxCalculationException(final String message) {
		super(message);
	}

	/**
	 * Creates a new {@code TaxCalculationException} object using the given message and cause exception.
	 * 
	 * @param message the reason for this {@code TaxCalculationException}.
	 * @param cause the {@code Throwable} that caused this {@code TaxCalculationException}.
	 */
	public TaxCalculationException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
