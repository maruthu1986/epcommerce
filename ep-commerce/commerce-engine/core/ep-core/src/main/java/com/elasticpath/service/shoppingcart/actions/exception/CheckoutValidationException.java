/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.actions.exception;

import java.util.Collection;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.structured.EpValidationException;

/**
 * Exception thrown when the shopping cart is in a state that doesn't allow check out.
 */
public class CheckoutValidationException extends EpValidationException {

	private static final long serialVersionUID = 3242534534534512398L;

	/**
	 * Constructor.
	 *
	 * @param message                 message
	 * @param structuredErrorMessages the detailed reason for this <code>UserStatusInactiveException</code>.
	 */
	public CheckoutValidationException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessages) {
		this(message, structuredErrorMessages, null);
	}


	/**
	 * Constructor.
	 *
	 * @param message                 message
	 * @param structuredErrorMessages the detailed reason for this <code>UserStatusInactiveException</code>.
	 * @param cause                   cause
	 */
	public CheckoutValidationException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessages,
									   final Throwable cause) {
		super(message, structuredErrorMessages, cause);
	}


	/**
	 * Constructor.
	 *
	 * @param structuredErrorMessages the detailed reason for this <code>UserStatusInactiveException</code>.
	 */
	public CheckoutValidationException(final Collection<StructuredErrorMessage> structuredErrorMessages) {
		this("Checkout validation error", structuredErrorMessages, null);
	}

}
