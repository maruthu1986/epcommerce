/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.validation.constraints.LengthConstraint;

/**
 * Length validator that checks whether string size is within given range.
 */
public class SimpleLengthValidator implements ConstraintValidator<LengthConstraint, String> {

	private int min;
	private int max;

	@Override
	public void initialize(final LengthConstraint lengthConstraint) {
		min = lengthConstraint.min();
		max = lengthConstraint.max();

		//sanity check
		validateMinMaxParameters();
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext constraintValidatorContext) {
		if (StringUtils.isBlank(value)) {
			return true;
		}
		int length = value.length();
		return length >= min && length <= max;
	}

	private void validateMinMaxParameters() {
		if (min < 0) {
			throw new IllegalArgumentException("Minimum value can't be negative");
		}
		if (max < 0) {
			throw new IllegalArgumentException("Maximum value can't be negative");
		}
		if (max < min) {
			throw new IllegalArgumentException("Maximum value can't be less than minimum");
		}
	}
}
