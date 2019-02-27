/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.formatting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.TimeZone;

/**
 * Provides an uniform way to display date & date time all across cm client.
 */
public interface UIDateTimeUtil {

	/**
	 * Formats date.
	 *
	 * @param date the date to be formatted
	 * @return the formatted date
	 */
	String formatAsDate(Date date);

	/**
	 * Formats date.
	 *
	 * @param date                   the date to be formatted
	 * @param nullDateRepresentation the value returned when date is null
	 * @return the formatted date
	 */
	String formatAsDate(Date date, String nullDateRepresentation);

	/**
	 * Formates date with the given timezone.
	 * @param date the date to be formatted.
	 * @param timezone the timezone to use.
	 * @return the formatted date.
	 */
	String formatAsDate(Date date, TimeZone timezone);

	/**
	 * Formats date & time.
	 *
	 * @param date the date to be formatted
	 * @return the formatted date
	 */
	String formatAsDateTime(Date date);

	/**
	 * Formats date & time.
	 *
	 * @param date                   the date to be formatted
	 * @param nullDateRepresentation the value returned when date is null
	 * @return the formatted date
	 */
	String formatAsDateTime(Date date, String nullDateRepresentation);

	/**
	 * Formats date and time with the given timezone.
	 * @param date the date to be formatted.
	 * @param timezone the timezone to use.
	 * @return the formatted date.
	 */
	String formatAsDateTime(Date date, TimeZone timezone);

	/**
	 * Get a formatted date.
	 *
	 * @param date the date to have a formatted representation
	 * @return a Date that is formatted
	 */
	Date getDateWithFormat(Date date);

	/**
	 * Parse a date string using the standard formatter.
	 *
	 * @param dateString the date string representation
	 * @return a Data
	 * @throws ParseException in case of parsing errors
	 */
	Date parseDate(String dateString) throws ParseException;

	/**
	 * Parse a date string using the standard formatter.
	 *
	 * @param dateString    the date string representation
	 * @param parsePosition the parse position object
	 * @return a Data
	 */
	Date parseDate(String dateString, ParsePosition parsePosition);

	/**
	 * Parse a date time string using the standard formatter.
	 *
	 * @param dateString    the date string representation
	 * @param parsePosition the parse position object
	 * @return a Data
	 */
	Date parseDateTime(String dateString, ParsePosition parsePosition);

	/**
	 * Parse a date time string using the standard formatter.
	 *
	 * @param dateString the date string representation
	 * @return a date
	 * @throws ParseException in case of parsing errors
	 */
	Date parseDateTime(String dateString) throws ParseException;

	/**
	 * Get the standard formatters.
	 *
	 * @return the standard formatters
	 */
	DateFormat[] getFormatters();


	/**
	 * Gets the client browser's timezone.
	 * @return the browser's timezone.
	 */
	TimeZone getTimeZoneFromBrowser();

	/**
	 * Initializes the user's timezone.
	 */
	void initializeTimezone();
}
