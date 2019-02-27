/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;

import com.elasticpath.validation.constraints.EpEmail;

/**
 * {@link EpEmail} validator for strings.
 */
public class EpEmailValidator implements ConstraintValidator<EpEmail, String> {

	@Override
	public void initialize(final EpEmail constraintAnnotation) {
		// do nothing
	}

	/**
	 * Determines whether a given {@code value} has an email format.
	 *
	 * @param value   value that is to be checked
	 * @return whether {@code value} has an email format
	 */
	protected boolean isEmailValid(final String value) {
		return EmailValidator.getInstance().isValid(value);
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (StringUtils.isBlank(value)) {
			return true;
		}
		return isEmailValid(value);
	}
}
