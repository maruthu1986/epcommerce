/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.test.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of common email content assertions.
 */
public final class EmailContentAssert {

	/**
	 * Private Constructor to prevent initialising this utility class.
	 */
	private EmailContentAssert() {
		// do nothing, no initialisation required
	}

	/**
	 * Asserts that the given email contents contain the order number.
	 *
	 * @param message the identifying message for the {@link AssertionError} (null okay)
	 * @param emailContents the email contents
	 * @param orderNumber the order number
	 */
	public static void assertEmailContentContainsOrderNumber(final String message, final String emailContents, final String orderNumber) {
		assertTrue(message, emailContents.contains(orderNumber));
	}
	
	/**
	 * Asserts that the given email contents contain the order shipment number.
	 * 
	 * @param message the identifying message for the {@link AssertionError} (null okay)
	 * @param emailContents the email contents
	 * @param orderNumber the order shipment number
	 */
	public static void assertEmailContentContainsOrderShipmentNumber(final String message, final String emailContents, final String orderNumber) {
		assertTrue(message, emailContents.contains(orderNumber));
	}

	/**
	 * Asserts that the given email contents do not contain any unresolved velocity code.
	 *
	 * @param message the identifying message for the {@link AssertionError} (null okay)
	 * @param emailContents the email contents
	 */
	public static void assertEmailContentDoesNotContainAnyUnresolvedVelocityCode(final String message, final String emailContents) {
		assertFalse(message, emailContents.contains("${"));

		// matches all dollar signs followed by a non-number, non-whitespace, non-newline character
		// eg. $foo would match; $123 would not.
		final String regex = "\\$[^0-9\\s\\n]+";

		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(emailContents);

		// performed outside the assertion to ensure the .group() call is called only if there is a match (otherwise an IllegalStateException is
		// thrown)
		final boolean matches = matcher.find();

		String assertionMessage = message;

		if (matches) {
			assertionMessage += " - Detected unrendered Velocity variable [" + matcher.group() + "]";
		}

		assertFalse(assertionMessage, matches);
	}

	/**
	 * Asserts that the given email subject is correct.
	 * 
	 * @param expectedSubject the expected email subject
	 * @param actualSubject the actual email subject
	 */
	public static void assertEmailSubjectCorrect(final String expectedSubject, final String actualSubject) {
		assertEquals("Email subject incorrect.", expectedSubject, actualSubject);
	}

	/**
	 * Asserts that the given email contents contain the expected text.
	 * 
	 * @param message the identifying message for the {@link AssertionError} (null okay)
	 * @param emailContents the email contents
	 * @param expectedText the expected text
	 */
	public static void assertEmailContentContainsExpectedText(final String message, final String expectedText, final String emailContents) {
		assertTrue(message, emailContents.contains(expectedText));
	}

}
