/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.validation.constraints.MultiOptionConstraint;

/**
 * Test class for {@link MultiOptionValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MultiOptionValidatorTest {

	@InjectMocks
	private MultiOptionValidator validator;

	@Mock
	private ConstraintValidatorContext validatorContext;

	@Mock
	private MultiOptionConstraint multiOptionAnnotation;

	private static final String[] MULTI_VALID_FIELD_OPTIONS = {"message", "id", "quantity"};

	/**
	 * Given a valid option, should return valid.
	 */
	@Test
	public void shouldBeValidGivenOneValidOption() {
		setUpValidOptions(MULTI_VALID_FIELD_OPTIONS);
		assertTrue("Expect valid given one valid option", validator.isValid("message", validatorContext));
	}

	/**
	 * Given multiple valid options, should return valid.
	 */
	@Test
	public void shouldBeValidGivenMultipleValidOptions() {
		setUpValidOptions(MULTI_VALID_FIELD_OPTIONS);
		assertTrue("Expect valid given multiple valid options", validator.isValid("message, id", validatorContext));
	}

	/**
	 * Given all valid options, should return valid.
	 */
	@Test
	public void shouldBeValidGivenAllValidOptions() {
		setUpValidOptions(MULTI_VALID_FIELD_OPTIONS);
		assertTrue("Expect valid given all valid options", validator.isValid("message, id, quantity", validatorContext));
	}

	/**
	 * Given valid options out of order, should return valid.
	 */
	@Test
	public void shouldBeValidGivenValidOptionsOutOfOrder() {
		setUpValidOptions(MULTI_VALID_FIELD_OPTIONS);
		assertTrue("Expect valid given valid options out of order", validator.isValid("id, message", validatorContext));
		assertTrue("Expect valid given valid options out of order", validator.isValid("quantity, id, message", validatorContext));
	}

	/**
	 * Given duplicate valid options, should return invalid.
	 */
	@Test
	public void shouldBeInvalidGivenDuplicateValidOptions() {
		setUpValidOptions(MULTI_VALID_FIELD_OPTIONS);
		assertFalse("Expect invalid given duplicate valid options", validator.isValid("message, message", validatorContext));
		assertFalse("Expect invalid given duplicate valid options", validator.isValid("id, message, id", validatorContext));
	}

	/**
	 * Given an invalid option, should return invalid.
	 */
	@Test
	public void shouldBeInvalidGivenInvalidOption() {
		setUpValidOptions(MULTI_VALID_FIELD_OPTIONS);
		assertFalse("Expect invalid given invalid option", validator.isValid("email", validatorContext));
		assertFalse("Expect invalid given invalid option", validator.isValid("email, message", validatorContext));
		assertFalse("Expect invalid given invalid options", validator.isValid("id, email, price, message", validatorContext));
	}

	/**
	 * Given empty option strings, should return valid.
	 */
	@Test
	public void shouldBeValidGivenNoOptions() {
		setUpValidOptions(MULTI_VALID_FIELD_OPTIONS);
		assertTrue("Expect valid given no options", validator.isValid("", validatorContext));
		assertTrue("Expect valid given no options", validator.isValid("  ", validatorContext));
	}

	/**
	 * Given null, should return valid.
	 */
	@Test
	public void shouldBeValidGivenNull() {
		setUpValidOptions(MULTI_VALID_FIELD_OPTIONS);
		assertTrue("Expect valid given no options", validator.isValid(null, validatorContext));
	}

	/**
	 * When there's no valid field options, should return valid.
	 */
	@Test
	public void shouldBeValidWhenValidFieldOptionsIsEmpty() {
		String[] noValidFieldOptions = {};
		setUpValidOptions(noValidFieldOptions);
		assertTrue(validator.isValid("email", validatorContext));
		assertTrue(validator.isValid("message, id", validatorContext));
	}

	/**
	 * When valid field options is null, should return valid.
	 */
	@Test
	public void shouldBeValidWhenValidFieldOptionsIsNull() {
		setUpValidOptions(null);
		assertTrue(validator.isValid("email", validatorContext));
		assertTrue(validator.isValid("message, quantity", validatorContext));
	}

	private void setUpValidOptions(final String[] validOptions) {
		when(multiOptionAnnotation.validFieldOptions()).thenReturn(validOptions);
		validator.initialize(multiOptionAnnotation);
	}


}