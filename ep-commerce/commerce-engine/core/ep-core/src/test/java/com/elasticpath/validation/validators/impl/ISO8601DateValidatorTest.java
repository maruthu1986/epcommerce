/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintValidatorContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test class for {@link ISO8601DateValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ISO8601DateValidatorTest {

	@InjectMocks
	private ISO8601DateValidator validator;

	@Mock
	private ConstraintValidatorContext validatorContext;

	private static final String DATE = "2016-08-18";

	private static final String TIME = "T10:15:30";

	private static final String OFFSET = "+04:00";

	/**
	 * A Date with ISO8601 standard should be valid.
	 */
	@Test
	public void shouldBeValidWithDate() {
		assertTrue("Expect valid with ISO8601 standard Date", validator.isValid(DATE, validatorContext));
	}

	/**
	 * A Date with incorrect date format should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithIncorrectDateFormat() {
		assertFalse("Expect invalid with format of (DD-HH-YYYY)", validator.isValid("18-09-2016", validatorContext));
		assertFalse("Expect invalid with incorrect format", validator.isValid("July 10, 2016", validatorContext));
		assertFalse("Expect invalid with month in date out of range", validator.isValid("2016-13-18", validatorContext));
		assertFalse("Expect invalid with day in date out of range", validator.isValid("2016-08-33", validatorContext));
		assertFalse("Expect invalid with year in date without four digits", validator.isValid("216-08-33", validatorContext));
		assertFalse("Expect invalid with month in date without two digits", validator.isValid("2016-8-33", validatorContext));
		assertFalse("Expect invalid with day in date without two digits", validator.isValid("2016-08-8", validatorContext));
	}

	/**
	 * A Date with time should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithTime() {
		assertFalse("Expect invalid for Date with time", validator.isValid(DATE + TIME, validatorContext));
		assertFalse("Expect invalid for Date with time", validator.isValid(DATE + TIME + "-", validatorContext));
	}

	/**
	 * A Date with offset should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithOffset() {
		assertFalse("Expect invalid for Date with positive offset", validator.isValid(DATE + OFFSET, validatorContext));
		assertFalse("Expect invalid for Date with negative offset", validator.isValid(DATE + "-04:00", validatorContext));
		assertFalse("Expect invalid for Date with UTC offset", validator.isValid(DATE + "Z", validatorContext));
		assertFalse("Expect invalid for Date with offset", validator.isValid(DATE + "T" + OFFSET, validatorContext));
	}

	/**
	 * A Date with time and offset should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithTimeAndOffset() {
		assertFalse("Expect invalid for Date with time and offset", validator.isValid(DATE + TIME + OFFSET, validatorContext));
	}

	/**
	 * A Date with any date delimiter not as dash should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithNonDashDateDelimiters() {
		assertFalse("Expect invalid with colons as date delimiters", validator.isValid("2016:08:10" + TIME + OFFSET, validatorContext));
		assertFalse("Expect invalid with commas as date delimiters", validator.isValid("2016,08,10" + TIME + OFFSET, validatorContext));
	}

	/**
	 * Empty string should be valid.
	 */
	@Test
	public void shouldBeValidWithEmptyString() {
		assertTrue("Expect valid with empty string", validator.isValid("", validatorContext));
		assertTrue("Expect valid with empty string", validator.isValid(" ", validatorContext));
	}

	/**
	 * Null should be valid.
	 */
	@Test
	public void shouldBeValidWithNull() {
		assertTrue("Expect valid with null", validator.isValid(null, validatorContext));
	}
}