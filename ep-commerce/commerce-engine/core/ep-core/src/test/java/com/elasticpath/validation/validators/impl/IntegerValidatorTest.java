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
 * Test class for {@link IntegerValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntegerValidatorTest {

	@InjectMocks
	private IntegerValidator validator;

	@Mock
	private ConstraintValidatorContext validatorContext;

	/**
	 * An integer string should be valid.
	 */
	@Test
	public void shouldBeValidWithIntegerString() {
		assertTrue("Expect valid with integer string", validator.isValid("11", validatorContext));
	}

	/**
	 * A decimal string should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithDecimalString() {
		assertFalse("Expect invalid with decimal string", validator.isValid("6445.34", validatorContext));
	}

	/**
	 * A integer string with spaces (1 245) should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithSpaceDelimiters() {
		assertFalse("Expect invalid with space as the delimiter", validator.isValid("1 245", validatorContext));
	}

	/**
	 * A integer string with underscore (1_245) should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithUnderscoreDelimiters() {
		assertFalse("Expect invalid with underscore as the delimiter", validator.isValid("1_245", validatorContext));
	}

	/**
	 * A integer string with comma (1,245) should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithCommaDelimiters() {
		assertFalse("Expect invalid with comma as the delimiter", validator.isValid("1,245", validatorContext));
	}

	/**
	 * A integer string with zeroes padded (00145) should be valid.
	 */
	@Test
	public void shouldBeValidWithZeroesPadded() {
		assertTrue("Expect valid with zeroes padded", validator.isValid("00145", validatorContext));
		assertTrue("Expect valid with zeroes padded", validator.isValid("0000055", validatorContext));
	}

	/**
	 * A positive integer string (+13) should be valid.
	 */
	@Test
	public void shouldBeValidWithPositivePrefix() {
		assertTrue("Expect valid with positive prefix", validator.isValid("+13", validatorContext));
	}

	/**
	 * A negative integer string (-13) should be valid.
	 */
	@Test
	public void shouldBeValidWithNegativePrefix() {
		assertTrue("Expect valid with negative prefix", validator.isValid("-13", validatorContext));
	}

	/**
	 * A string with alphabetical characters should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithAlphabets() {
		assertFalse("Expect invalid with alphabets", validator.isValid("12t", validatorContext));
		assertFalse("Expect invalid with alphabets", validator.isValid("b12", validatorContext));
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