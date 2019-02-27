/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.bval.constraints.EmailValidator;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.validation.constraints.CustomerUsernameUserIdModeEmail;

/**
 * Validator for checking the required state of a Customer username.
 */
public class CustomerUsernameUserIdModeEmailValidator implements ConstraintValidator<CustomerUsernameUserIdModeEmail, Customer> {

	/** The path that will be generated with validation fails. */
	public static final String VALIDATION_PATH = "username";

	/** The Property Key that the error message will use. */
	public static final String VALIDATION_MESSAGE_KEY = "com.elasticpath.validation.constraints.CustomerUsernameUserIdModeEmail.message";

	@Override
	public void initialize(final CustomerUsernameUserIdModeEmail constraintAnnotation) {
		// do nothing
	}

	/**
	 * {@inheritDoc} <br>
	 * Validates that the username conforms to the {@link EmailValidator} when user ID mode is either in
	 * {@link WebConstants#USE_EMAIL_AS_USER_ID_MODE} or {@link WebConstants#GENERATE_UNIQUE_PERMANENT_USER_ID_MODE}.
	 */
	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		boolean valid = true;
		int userIdMode = customer.getUserIdMode();

		if (userIdMode == WebConstants.USE_EMAIL_AS_USER_ID_MODE || userIdMode == WebConstants.GENERATE_UNIQUE_PERMANENT_USER_ID_MODE) {
			valid = new EmailValidator().isValid(customer.getUsername(), context);
			if (!valid) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(VALIDATION_MESSAGE_KEY)
						.addNode(VALIDATION_PATH)
						.addConstraintViolation();
			}
		}

		return valid;
	}
}
