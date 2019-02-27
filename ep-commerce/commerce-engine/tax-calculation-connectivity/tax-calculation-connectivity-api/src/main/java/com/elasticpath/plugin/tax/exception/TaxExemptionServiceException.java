/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.plugin.tax.exception;

/**
 * Thrown when the service does not provide Tax Exemption Capability.
 */
public class TaxExemptionServiceException extends TaxCalculationException {

	/** Serial version id. */
	private static final long serialVersionUID = 7000000001L;
	/**
	 * Constructs a new exception.
	 *
	 * @param message the message
	 */
	public TaxExemptionServiceException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new exception.
	 *
	 * @param message the message
	 * @param cause the cause of the exception
	 */
	public TaxExemptionServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
