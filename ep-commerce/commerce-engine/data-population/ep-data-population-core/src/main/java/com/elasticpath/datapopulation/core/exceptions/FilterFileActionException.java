/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.exceptions;

/**
 * A {@link DataPopulationActionException} subclass to indicate an exception occurred processing the request.
 */
public class FilterFileActionException extends DataPopulationActionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public FilterFileActionException() {
		super();
	}

	/**
	 * Constructor taking in a specific exception message.
	 *
	 * @param message the message to use.
	 */
	public FilterFileActionException(final String message) {
		super(message);
	}

	/**
	 * Constructor taking in the nested cause.
	 *
	 * @param cause the nested cause of the exception.
	 */
	public FilterFileActionException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor taking in a specific exception message and nested cause.
	 *
	 * @param message the message to use
	 * @param cause   the nested cause of the exception.
	 */
	public FilterFileActionException(final String message, final Throwable cause) {
		super(message, cause);
	}
}