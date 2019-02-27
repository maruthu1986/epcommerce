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
 * Test class for {@link DecimalValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DecimalValidatorTest {

	@InjectMocks
	private DecimalValidator validator;

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
	 * A decimal string should be valid.
	 */
	@Test
	public void shouldBeValidWithDecimalString() {
		assertTrue("Expect valid with decimal string", validator.isValid("6445.34", validatorContext));
	}

	/**
	 * A decimal string with spaces (1 245.99) should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithSpaceDelimiters() {
		assertFalse("Expect invalid with space as the delimiter", validator.isValid("1 245.99", validatorContext));
	}

	/**
	 * A decimal string with underscore (1_245.99) should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithUnderscoreDelimiters() {
		assertFalse("Expect invalid with underscore as the delimiter", validator.isValid("1_245.99", validatorContext));
	}

	/**
	 * A decimal string with comma (1,245.99) should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithCommaDelimiters() {
		assertFalse("Expect invalid with comma as the delimiter", validator.isValid("1,245.99", validatorContext));
		assertFalse("Expect invalid with comma as the delimiter", validator.isValid("1245,99", validatorContext));
	}

	/**
	 * A integer string with zeroes padded (00145.54) should be valid.
	 */
	@Test
	public void shouldBeValidWithZeroesPadded() {
		assertTrue("Expect valid with zeroes pre-padded", validator.isValid("00145.54", validatorContext));
		assertTrue("Expect valid with zeroes pre-padded", validator.isValid("000000145.54", validatorContext));
		assertTrue("Expect valid with zeroes post-padded", validator.isValid("457.10", validatorContext));
		assertTrue("Expect valid with zeroes post-padded", validator.isValid("457.10000", validatorContext));
	}

	/**
	 * A positive decimal string (+13.99) should be valid.
	 */
	@Test
	public void shouldBeValidWithPositivePrefix() {
		assertTrue("Expect valid with positive prefix", validator.isValid("+13.99", validatorContext));
	}

	/**
	 * A negative decimal string (-13.99) should be valid.
	 */
	@Test
	public void shouldBeValidWithNegativePrefix() {
		assertTrue("Expect valid with negative prefix", validator.isValid("-13.99", validatorContext));
	}

	/**
	 * A string with alphabetical characters should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithAlphabets() {
		assertFalse("Expect invalid with alphabets", validator.isValid("12.a", validatorContext));
		assertFalse("Expect invalid with alphabets", validator.isValid("b12.4", validatorContext));
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