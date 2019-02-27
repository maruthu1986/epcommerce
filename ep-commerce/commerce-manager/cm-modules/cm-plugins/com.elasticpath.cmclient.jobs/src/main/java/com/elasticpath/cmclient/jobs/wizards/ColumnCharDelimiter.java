/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.jobs.wizards;

import com.elasticpath.cmclient.jobs.JobsMessages;


/**
 * Column character delimiter enumeration.
 */
public enum ColumnCharDelimiter {
	/**
	 * Comma column delimiter.
	 */
	COMMA(JobsMessages.get().ColumnDelimiter_Comma, ','),
	/**
	 * Semicolon column delimiter.
	 */
	SEMICOLON(JobsMessages.get().ColumnDelimiter_Semicolon, ';'),
	/**
	 * Space column delimiter.
	 */
	SPACE(JobsMessages.get().ColumnDelimiter_Space, ' '),
	/**
	 * Tab column delimiter.
	 */
	TAB(JobsMessages.get().ColumnDelimiter_Tab, '\t'),
	/**
	 * Other column delimiter.
	 */
	OTHER(JobsMessages.get().ColumnDelimiter_Other, Character.MIN_VALUE);

	private final String message;

	private final char delimiter;

	/**
	 * Constructor for a column character delimiter.
	 * 
	 * @param message the delimiter's localized message
	 * @param delimiter the delimiter character
	 */
	ColumnCharDelimiter(final String message, final char delimiter) {
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
