/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Thrown when no matching {@link com.elasticpath.converter.StringToTypeConverter StringToTypeConverter} is available for the setting's type.
 */
public class UnexpectedSettingValueTypeException extends EpServiceException {

	private static final long serialVersionUID = 8002370217117299736L;

	/**
	 * Constructor with exception message.
	 *
	 * @param message the message
	 */
	public UnexpectedSettingValueTypeException(final String message) {
		super(message);
	}

	/**
	 * Constructor with exception message and cause.
	 *
	 * @param message the message
	 * @param cause the causing exception
	 */
	public UnexpectedSettingValueTypeException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
