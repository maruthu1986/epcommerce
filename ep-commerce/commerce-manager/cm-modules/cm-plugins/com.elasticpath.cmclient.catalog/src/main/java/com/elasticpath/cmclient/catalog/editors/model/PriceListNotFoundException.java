/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.editors.model;

/**
 * Exception indicating that a problem has occurred with attempting to retrieve a price list but the price list has not been found.
 */
public class PriceListNotFoundException extends RuntimeException {

	/**
	 * Constructs price list not found exception.
	 */
	public PriceListNotFoundException() {
		super();
	}

	/**
	 * Constructs price list not found exception with given message.
	 * 
	 * @param message message
	 */
	public PriceListNotFoundException(final String message) {
		super(message);
	}
}
