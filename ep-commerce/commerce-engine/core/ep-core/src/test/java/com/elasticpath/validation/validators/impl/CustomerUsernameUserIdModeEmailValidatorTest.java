/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintValidatorContext;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.test.factory.TestCustomerProfileFactory;

/**
 * Test class for {@link CustomerUsernameUserIdModeEmailValidator}.
 */
public class CustomerUsernameUserIdModeEmailValidatorTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CustomerImpl customer;
	private CustomerUsernameUserIdModeEmailValidator validator;

	private ConstraintValidatorContext validatorContext;

	private static final String USERNAME = "hferents@redhat.com";

	/** Test initialization. */
	@Before
	public void setUp() {
		customer = new CustomerImpl();
		customer.setCustomerProfileAttributes(new TestCustomerProfileFactory().getProfile());

		validatorContext = context.mock(ConstraintValidatorContext.class);
		context.checking(new Expectations() {
			{
				allowing(validatorContext);
			}
		});

		validator = new CustomerUsernameUserIdModeEmailValidator();
	}

	/**
	 * Should fail without a user id mode defined.
	 */
	@Test(expected = NullPointerException.class)
	public void testNoService() {
		validator.isValid(null, null);
	}

	/**
	 * Validation should pass without caring about the format when the userIdMode is set to INDEPENDANT_EMAIL_AND_USER_ID_MODE[3].
	 */
	@Test
	public void testValidationPassesWhenUsingIndependantEmailAndUserIdMode() {
		shouldHaveUserIdMode(WebConstants.INDEPENDANT_EMAIL_AND_USER_ID_MODE);
		customer.setUserId("NON_EMAIL_BASED_USER_ID");
		customer.setEmail("NON_EMAIL_BASED_USER_ID");

		assertTrue("Validation should not fail when not using USE_EMAIL_AS_USER_ID_MODE.",
				validator.isValid(customer, validatorContext));
	}

	/**
	 * Test validation when using USE_EMAIL_AS_USER_ID_MODE with a valid email address.
	 */
	@Test
	public void testValidationWhenUsingEmailAsUserIdModeWithAValidEmail() {
		shouldHaveUserIdMode(WebConstants.USE_EMAIL_AS_USER_ID_MODE);
		customer.setUserId(USERNAME);
		customer.setEmail(customer.getUserId());

		assertTrue("Validation should pass in USE_EMAIL_AS_USER_ID_MODE with a valid email address.",
				validator.isValid(customer, validatorContext));
	}

	/**
	 * Test validation when using USE_EMAIL_AS_USER_ID_MODE with an invalid email address.
	 */
	@Test
	public void testValidationWhenUsingEmailAsUserIdModeWithAnInvalidEmail() {
		shouldHaveUserIdMode(WebConstants.USE_EMAIL_AS_USER_ID_MODE);
		customer.setUserId("Hardy");
		customer.setEmail(customer.getUserId());

		assertFalse("Validation should fail in USE_EMAIL_AS_USER_ID_MODE with an invalid email address.",
				validator.isValid(customer, validatorContext));
	}

	/**
	 * Test validation when using GENERATE_UNIQUE_PERMANENT_USER_ID_MODE with a valid email address.
	 */
	@Test
	public void testValidationWhenUsingGenerateUniquePermanentUserIdModeWithAValidEmail() {
		shouldHaveUserIdMode(WebConstants.GENERATE_UNIQUE_PERMANENT_USER_ID_MODE);
		customer.setUserId(USERNAME);
		customer.setEmail(USERNAME);

		assertTrue("Validation should pass in GENERATE_UNIQUE_PERMANENT_USER_ID_MODE with a valid email address.",
				validator.isValid(customer, validatorContext));
	}

	/**
	 * Test validation when using GENERATE_UNIQUE_PERMANENT_USER_ID_MODE with an invalid email address.
	 */
	@Test
	public void testValidationWhenUsingGenerateUniquePermanentUserIdModeWithAnInvalidEmail() {
		shouldHaveUserIdMode(WebConstants.GENERATE_UNIQUE_PERMANENT_USER_ID_MODE);
		customer.setUserId("Hardy");
		customer.setEmail(customer.getUserId());

		assertFalse("Validation should fail in GENERATE_UNIQUE_PERMANENT_USER_ID_MODE with an invalid email address.",
				validator.isValid(customer, validatorContext));
	}

	@Test
	public void testValidationPathIsSetToUserNameWhenUsernameIsInvalid() {
		final ConstraintValidatorContext validatorContext = context.mock(ConstraintValidatorContext.class, "vc");

		shouldHaveUserIdMode(WebConstants.USE_EMAIL_AS_USER_ID_MODE);
		customer.setEmail("foo-is-not-a-valid-email-address");

		context.checking(new Expectations() {
			{
				final ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder =
						context.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
				final ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext nodeViolationBuilder =
						context.mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext.class);

				oneOf(validatorContext).disableDefaultConstraintViolation();
				oneOf(validatorContext).buildConstraintViolationWithTemplate(with(any(String.class)));
				will(returnValue(violationBuilder));

				oneOf(violationBuilder).addNode(CustomerUsernameUserIdModeEmailValidator.VALIDATION_PATH);
				will(returnValue(nodeViolationBuilder));
				oneOf(nodeViolationBuilder).addConstraintViolation();
			}
		});

		assertFalse("Sanity Check", validator.isValid(customer, validatorContext));
	}

	private void shouldHaveUserIdMode(final int userIdMode) {
		customer.setUserIdMode(userIdMode);
	}
}
