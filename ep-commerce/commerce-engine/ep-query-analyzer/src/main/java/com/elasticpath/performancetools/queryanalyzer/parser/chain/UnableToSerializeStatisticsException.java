/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.performancetools.queryanalyzer.parser.chain;

/**
 * Unable to serialize exception.
 */
public class UnableToSerializeStatisticsException extends RuntimeException {

	private static final long serialVersionUID = -1L;

	/**
	 * Default constructor.
	 *
	 * @param cause basic cause.
	 */
	public UnableToSerializeStatisticsException(final Throwable cause) {
		super(cause);
	}
}
