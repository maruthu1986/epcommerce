/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.validation.constraints.BooleanConstraint;

/**
 * Boolean validator. Can be used by any JSR-303/349 validation framework.
 * The String value being validated is valid only if actual value is strictly "true" or "false".
 */
public class BooleanValidator implements ConstraintValidator<BooleanConstraint, String> {
	@Override
	public void initialize(final BooleanConstraint constraintAnnotation) {
		//nothing
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (StringUtils.isBlank(value)) {
			return true;
		}
		return "true".equals(value) || "false".equals(value);
	}
}
