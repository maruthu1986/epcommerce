/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.catalog.exception;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Exception thrown if the theme template drop down box is unable to be
 * populated with entries.
 */
public class ThemeTemplateComboPopulationFailedException extends EpServiceException {

	/**
	 * Exception created with a message and a cause.
	 * @param message exception message
	 * @param cause exception cause
	 */
	public ThemeTemplateComboPopulationFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Exception created with a message.
	 * @param message exception message
	 */
	public ThemeTemplateComboPopulationFailedException(final String message) {
		super(message);
	}

}
