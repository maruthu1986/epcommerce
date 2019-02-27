/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.search.index;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Provides a bunch of analyzing methods.
 */
public interface Analyzer {

	/**
	 * Analyzes the given date.
	 *
	 * @param date the date to analyze
	 * @return the analyzed text
	 */
	String analyze(Date date);

	/**
	 * Analyzes the given int.
	 *
	 * @param value the int value to analyze
	 * @return the analyzed text
	 */
	String analyze(int value);

	/**
	 * Analyzes the given long.
	 *
	 * @param value the long value to analyze
	 * @return the analyzed text
	 */
	String analyze(long value);

	/**
	 * Analyzes the given string. Returns a trimmed instance of the string if not null, otherwise
	 * the empty string.
	 *
	 * @param value the string value to analyze
	 * @return the analyzed text
	 */
	String analyze(String value);

	/**
	 * Analyzes the given string. Returns a trimmed instance of the string if not null, otherwise
	 * the empty string.  Also escapes all quotes in the string.
	 *
	 * @param value the string value to analyze
	 * @param isEscapeAllQuotes flag to force all quotes, matched and unmatched, to be escaped
	 * @return the analyzed text
	 */
	String analyze(String value, boolean isEscapeAllQuotes);

	/**
	 * Analyzes the given BigDecimal value.
	 *
	 * @param value BigDecimal value to analyze
	 * @return the analyzed text
	 */
	String analyze(BigDecimal value);
}
