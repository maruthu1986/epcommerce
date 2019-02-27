/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.exception;


/**
 * Generic exception indicating that a problem has occurred in the Catalog plugin.
 * This can be used to wrap checked exceptions with this unchecked one.
 */
public class EpCatalogException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message the message 
	 * @param cause the wrapped exception
	 */
	public EpCatalogException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructor.
	 *
	 * @param cause the wrapped exception
	 */
	public EpCatalogException(final Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructor.
	 *
	 * @param message the message 
	 * 
	 */
	public EpCatalogException(final String message) {
		super(message);
	}
	
}
