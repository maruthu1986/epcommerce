/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.payment;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Payment exception.
 */
public class PaymentServiceException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * @param message the message to be set
	 */
	public PaymentServiceException(final String message) {
		super(message);
	}

	/**
	 * @param message the message to be set
	 * @param cause the cause exception to be wrapped
	 */
	public PaymentServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
