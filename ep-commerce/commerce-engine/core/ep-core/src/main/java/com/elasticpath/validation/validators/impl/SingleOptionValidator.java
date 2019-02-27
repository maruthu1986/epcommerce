/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.ArrayUtils;

import com.elasticpath.validation.constraints.SingleOptionConstraint;
import com.elasticpath.validation.validators.util.MultiOptionHandler;

/**
 * Single-option validator. Can be used by any JSR-303/349 validation framework.
 * The input String value, representing selected option , is validated
 * against valid field options.
 */
public class SingleOptionValidator implements ConstraintValidator<SingleOptionConstraint, String> {

	private String[] validFieldOptions;

	@Override
	public void initialize(final SingleOptionConstraint constraintAnnotation) {
		validFieldOptions = constraintAnnotation.validFieldOptions();
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		return MultiOptionHandler.isOptionsEmptyBlankOrNull(value, validFieldOptions) || ArrayUtils.contains(validFieldOptions, value);
	}
}
