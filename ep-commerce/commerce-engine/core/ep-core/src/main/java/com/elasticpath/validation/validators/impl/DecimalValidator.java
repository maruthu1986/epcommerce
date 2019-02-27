/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.validation.constraints.DecimalConstraint;

/**
 * Decimal validator. Can be used by any JSR-303/349 validation framework.
 * The String value being validated is valid only if actual value can be parsed as a decimal number.
 */
public class DecimalValidator implements ConstraintValidator<DecimalConstraint, String> {
	@Override
	public void initialize(final DecimalConstraint constraintAnnotation) {
		//nothing
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (StringUtils.isBlank(value)) {
			return true;
		}

		try {
			DatatypeConverter.parseDecimal(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
