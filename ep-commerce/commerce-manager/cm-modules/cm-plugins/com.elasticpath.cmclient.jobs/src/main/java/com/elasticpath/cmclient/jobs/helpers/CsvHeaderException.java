/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.jobs.helpers;

/**
 * An exception for header validation.
 *
 */
public class CsvHeaderException extends Exception {
	private static final long serialVersionUID = 8485437351826271567L;

	/**
	 * A default constructor.
	 */
	public CsvHeaderException() {
		super();
	}

	/**
	 * @param message error message
	 */
	public CsvHeaderException(final String message) {
		super(message);
	}

	/**
	 * @param cause throwable cause
	 */
	public CsvHeaderException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message error message
	 * @param cause throwable cause
	 */
	public CsvHeaderException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
