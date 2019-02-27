package com.elasticpath.selenium.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.text.RandomStringGenerator;

import com.elasticpath.selenium.framework.util.PropertyManager;

/**
 * Utility class for commonly used method.
 */
public final class Utility {
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Utility.class);

	/**
	 * Constructor.
	 */
	private Utility() {
	}

	/**
	 * Returns a random UUID of length UUID_END_INDEX.
	 *
	 * @return a random UUID
	 */
	public static String getRandomUUID() {
		return UUID.randomUUID().toString().substring(0, Constants.UUID_END_INDEX);
	}

	/**
	 * Generates a random string of specified length consisting of only lower case english alphabets a-z.
	 *
	 * @param length the length of random string to be generated
	 * @return a random UUID
	 */
	public static String generateRandomAlphabets(final int length) {
		final RandomStringGenerator generator = new RandomStringGenerator.Builder()
				.withinRange('a', 'z').build();
		return generator.generate(length);
	}

	/**
	 * Converts string to Date.
	 * @param dateString String
	 * @return Date
	 */
	public static Date convertToDate(final String dateString) {
		String dateFormat = PropertyManager.getInstance().getProperty("ep.dateFormat");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.ENGLISH);

		Date actualDate = null;
		try {
			actualDate = simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			LOGGER.debug(e.getMessage());
		}
		return actualDate;
	}
}
