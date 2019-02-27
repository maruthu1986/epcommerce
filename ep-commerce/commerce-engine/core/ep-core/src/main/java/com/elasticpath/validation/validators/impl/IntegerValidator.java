/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.validation.constraints.IntegerConstraint;

/**
 * Integer validator. Can be used by any JSR-303/349 validation framework.
 * The String value being validated is valid only if actual value can be parsed as an integer number.
 */
public class IntegerValidator implements ConstraintValidator<IntegerConstraint, String> {
	@Override
	public void initialize(final IntegerConstraint constraintAnnotation) {
		//nothing
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {

		if (StringUtils.isBlank(value)) {
			return true;
		}

		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
