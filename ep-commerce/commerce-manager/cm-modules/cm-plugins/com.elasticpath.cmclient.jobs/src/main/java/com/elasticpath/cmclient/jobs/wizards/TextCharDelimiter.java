/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.jobs.wizards;

import com.elasticpath.cmclient.jobs.JobsMessages;


/**
 * Text character delimiter enumeration.
 */
public enum TextCharDelimiter {
	/**
	 * Double quote text delimiter.
	 */
	DOUBLE_QUOTE(JobsMessages.get().TextDelimiter_DoubleQuote, '\"'),
	/**
	 * Single quote text delimiter.
	 */
	SINGLE_QUOTE(JobsMessages.get().TextDelimiter_SingleQuote, '\''),
	/**
	 * Other column delimiter.
	 */
	OTHER(JobsMessages.get().TextDelimiter_Other, Character.MIN_VALUE);

	private final String message;

	private final char delimiter;

	/**
	 * Constructor for a text character delimiter.
	 * 
	 * @param message the delimiter's localized message
	 * @param delimiter the delimiter character
	 */
	TextCharDelimiter(final String message, final char delimiter) {
		this.message = message;
		this.delimiter = delimiter;
	}

	/**
	 * Returns the delimiter's localized message.
	 * 
	 * @return the delimiter's localized message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the delimiter character.
	 * 
	 * @return the delimiter character
	 */
	public char getDelimiter() {
		return delimiter;
	}
}
