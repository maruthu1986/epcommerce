/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui;

/**
/**
 * Exception indicating inability of scanning plugin.xml file.
 */
public class ExtensionProcessingException extends RuntimeException {

	/**
	 * Constructs the exception.
	 *
	 * @param message the exception message
	 */
	public ExtensionProcessingException(final String message) {
		super(message);
	}

	/**
	 * Constructs the exception.
	 * @param message the message.
	 * @param cause the cause.
	 */
	public ExtensionProcessingException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
