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
 * Test class for {@link ISO8601DateTimeValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ISO8601DateTimeValidatorTest {

	@InjectMocks
	private ISO8601DateTimeValidator validator;

	@Mock
	private ConstraintValidatorContext validatorContext;

	private static final String DATE = "2016-08-18";

	private static final String TIME = "T10:15:30";

	private static final String OFFSET = "+04:00";

	/**
	 * A DateTime with ISO8601 standard should be valid.
	 */
	@Test
	public void shouldBeValidWithDateTimeOffset() {
		assertTrue("Expect valid with ISO8601 standard DateTime", validator.isValid(DATE + TIME + OFFSET, validatorContext));
		assertTrue("Expect valid with ISO8601 standard DateTime", validator.isValid(DATE + TIME + "Z", validatorContext));
	}

	/**
	 * A DateTime with incorrect date format should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithIncorrectDateFormat() {
		assertFalse("Expect invalid with format of (DD-HH-YYYY)", validator.isValid("10-08-2016" + TIME + OFFSET, validatorContext));
		assertFalse("Expect invalid with incorrect format", validator.isValid("July 10, 2016" + TIME + OFFSET, validatorContext));
		assertFalse("Expect invalid with month in date out of range", validator.isValid("2016-13-18" + TIME + OFFSET, validatorContext));
		assertFalse("Expect invalid with day in date out of range", validator.isValid("2016-08-33" + TIME + OFFSET, validatorContext));
		assertFalse("Expect invalid with year in date without four digits", validator.isValid("216-08-33" + TIME + OFFSET, validatorContext));
		assertFalse("Expect invalid with month in date without two digits", validator.isValid("2016-8-33" + TIME + OFFSET, validatorContext));
		assertFalse("Expect invalid with day in date without two digits", validator.isValid("2016-08-8" + TIME + OFFSET, validatorContext));
	}

	/**
	 * A DateTime with incorrect time format should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithIncorrectTimeFormat() {
		assertFalse("Expect invalid with hour in time out of range", validator.isValid(DATE + "T45:15:30" + OFFSET, validatorContext));
		assertFalse("Expect invalid with minute in time out of range", validator.isValid(DATE + "T10:76:30" + OFFSET, validatorContext));
		assertFalse("Expect invalid with second in time out of range", validator.isValid(DATE + "T10:15:96" + OFFSET, validatorContext));
		assertFalse("Expect invalid with hour in time without two digits", validator.isValid(DATE + "T8:15:30" + OFFSET, validatorContext));
		assertFalse("Expect invalid with minute in time without two digits", validator.isValid(DATE + "T10:5:30" + OFFSET, validatorContext));
		assertFalse("Expect invalid with second in time without two digits", validator.isValid(DATE + "T10:15:3" + OFFSET, validatorContext));
	}

	/**
	 * A DateTime with incorrect offset format should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithIncorrectOffsetFormat() {
		assertFalse("Expect invalid with hour in offset out of range", validator.isValid(DATE + TIME + "+57:00", validatorContext));
		assertFalse("Expect invalid with minute in offset out of range", validator.isValid(DATE + TIME + "+04:86", validatorContext));
	}

	/**
	 * A DateTime without date should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithoutDate() {
		assertFalse("Expect invalid for DateTime without an date", validator.isValid(TIME + OFFSET, validatorContext));
		assertFalse("Expect invalid for DateTime without an date", validator.isValid("10:15:30" + OFFSET, validatorContext));
	}

	/**
	 * A DateTime without time should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithoutTime() {
		assertFalse("Expect invalid for DateTime without an time", validator.isValid(DATE + OFFSET, validatorContext));
		assertFalse("Expect invalid for DateTime without an time", validator.isValid(DATE + "T" + OFFSET, validatorContext));
	}

	/**
	 * A DateTime without offset should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithoutOffset() {
		assertFalse("Expect invalid for DateTime without an offset", validator.isValid(DATE + TIME, validatorContext));
		assertFalse("Expect invalid for DateTime without an offset", validator.isValid(DATE + TIME + "-", validatorContext));
	}

	/**
	 * A DateTime with any date delimiter not as dash should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithNonDashDateDelimiters() {
		assertFalse("Expect invalid with colons as date delimiters", validator.isValid("2016:08:10" + TIME + OFFSET, validatorContext));
		assertFalse("Expect invalid with commas as date delimiters", validator.isValid("2016,08,10" + TIME + OFFSET, validatorContext));
	}

	/**
	 * A DateTime with any time delimiter not as colon should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithNonCommaTimeDelimiters() {
		assertFalse("Expect invalid with dashes as time delimiters", validator.isValid(DATE + "T10-15-30" + OFFSET, validatorContext));
		assertFalse("Expect invalid with commas as time delimiters", validator.isValid(DATE + "T10,15,30" + OFFSET, validatorContext));
	}

	/**
	 * A DateTime without lowercase date-time delimiter or UTC Z should be valid.
	 */
	@Test
	public void shouldBeValidWithCaseInsensitive() {
		assertTrue("Expect valid with date-time delimiter as lowercase T", validator.isValid(DATE + "t10:15:30" + OFFSET, validatorContext));
		assertTrue("Expect valid with UTC as lowercase Z", validator.isValid(DATE + TIME + "z", validatorContext));
	}

	/**
	 * A DateTime with positive or negative offsets should be valid.
	 */
	@Test
	public void shouldBeValidWithNegativePositiveOffsets() {
		assertTrue("No violation when given a positive offset", validator.isValid(DATE + TIME + "+12:00", validatorContext));
		assertTrue("No violation when given a negative offset", validator.isValid(DATE + TIME + "-08:00", validatorContext));
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