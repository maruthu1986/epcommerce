/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;

/**
 * {@link MissingShippingOptionException} thrown when {@link com.elasticpath.shipping.connectivity.dto.ShippingOption} is not set
 * on {@link com.elasticpath.domain.shoppingcart.ShoppingCart}.
 */
public class MissingShippingOptionException extends EpSystemException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The constructor.
	 *
	 * @param message the message
	 */
	public MissingShippingOptionException(final String message) {
		super(message);
	}

	/**
	 * Constructor with a throwable.
	 *
	 * @param message   the message
	 * @param throwable the cause
	 */
	public MissingShippingOptionException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
