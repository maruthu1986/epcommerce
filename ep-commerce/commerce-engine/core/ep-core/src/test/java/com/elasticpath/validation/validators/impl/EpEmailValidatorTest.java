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
 * Test class for {@link EpEmailValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EpEmailValidatorTest {

	@InjectMocks
	private EpEmailValidator validator;

	@Mock
	private ConstraintValidatorContext validatorContext;

	/**
	 * A correctly formatted email should be valid.
	 */
	@Test
	public void shouldBeValidWithValidEmail() {
		assertTrue("Expect valid given email with correct format", validator.isValid("harry.potter@elasticpath.com", validatorContext));
		assertTrue("Expect valid given email with correct format", validator.isValid("johnsmith@elasticpath.com", validatorContext));
	}

	/**
	 * An email with incorrect domain should be invalid.
	 */
	@Test
	public void shouldBeInvalidGivenEmailWithIncorrectDomain() {
		assertFalse("Expect invalid given email without domain", validator.isValid("harry.potter@", validatorContext));
		assertFalse("Expect invalid given email with incorrect domain", validator.isValid("harry.potter@elasticpath", validatorContext));
		assertFalse("Expect invalid given email with incorrect domain", validator.isValid("harry.potter@.com", validatorContext));
	}

	/**
	 * An email with incorrect user part.
	 */
	@Test
	public void shouldBeInvalidGivenEmailWithIncorrectUser() {
		assertFalse("Expect invalid given email without user", validator.isValid("@elasticpath.com", validatorContext));
		assertFalse("Expect invalid given email with incorrect user", validator.isValid("harry.\"pot\"ter@elasticpath.com", validatorContext));
		assertFalse("Expect invalid given email with incorrect user", validator.isValid("harry..potter@elasticpath.com", validatorContext));
	}

	/**
	 * An email without @ symbol should be invalid.
	 */
	@Test
	public void shouldBeInvalidWithEmailWithoutAtSymbol() {
		assertFalse("Expect invalid given email without @ symbol", validator.isValid("harry.potter elasticpath.com", validatorContext));
		assertFalse("Expect invalid given email without @ symbol", validator.isValid("harry.potter#elasticpath.com", validatorContext));
		assertFalse("Expect invalid given email without @ symbol", validator.isValid("harry.potter1elasticpath.com", validatorContext));
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
