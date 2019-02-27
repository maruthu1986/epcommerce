/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elasticpath.validation.constraints.MultiOptionConstraint;
import com.elasticpath.validation.validators.util.MultiOptionHandler;

/**
 * Multi-option validator. Can be used by any JSR-303/349 validation framework.
 * The input String value, representing selected options in CSV format, is validated
 * against valid field options.
 */
public class MultiOptionValidator implements ConstraintValidator<MultiOptionConstraint, String> {

	private String[] validFieldOptions;

	@Override
	public void initialize(final MultiOptionConstraint constraintAnnotation) {
		validFieldOptions = constraintAnnotation.validFieldOptions();
	}

	@Override
	public boolean isValid(final String inputCSV, final ConstraintValidatorContext context) {

		final String[] invalidOptions = MultiOptionHandler.getInvalidOptions(inputCSV, validFieldOptions);

		return invalidOptions == null || invalidOptions.length == 0;
	}
}
