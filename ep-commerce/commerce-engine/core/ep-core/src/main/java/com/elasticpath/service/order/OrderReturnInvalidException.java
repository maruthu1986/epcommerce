/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.order;

/**
 * Order return validation error.
 */
public class OrderReturnInvalidException extends RuntimeException {

	private static final long serialVersionUID = 5000000001L;

	/**
	 * Constructor.
	 * 
	 * @param message the message
	 */
	public OrderReturnInvalidException(final String message) {
		super(message);
	}
}
