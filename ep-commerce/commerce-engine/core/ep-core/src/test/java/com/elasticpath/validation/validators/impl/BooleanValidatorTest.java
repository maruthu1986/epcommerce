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
 * Test class for {@link BooleanValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BooleanValidatorTest {

	@InjectMocks
	private BooleanValidator validator;

	@Mock
	private ConstraintValidatorContext validatorContext;

	/**
	 * A true should be valid.
	 */
	@Test
	public void shouldBeValidWithNonCapitalizedTrueString() {
		assertTrue("Expect valid with true", validator.isValid("true", validatorContext));
	}

	/**
	 * A True should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithCapitalizedTrueString() {
		assertFalse("Expect invalid with True", validator.isValid("True", validatorContext));
	}

	/**
	 * A false should be valid.
	 */
	@Test
	public void shouldBeValidWithNonCapitalizedFalseString() {
		assertTrue("Expect valid with false", validator.isValid("false", validatorContext));
	}

	/**
	 * A False should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithCapitalizedFalseString() {
		assertFalse("Expect invalid with False", validator.isValid("False", validatorContext));
	}

	/**
	 * Any string other than true or false should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithNonBooleanString() {
		assertFalse("Expect invalid with any string other than true or false", validator.isValid("blue123", validatorContext));
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