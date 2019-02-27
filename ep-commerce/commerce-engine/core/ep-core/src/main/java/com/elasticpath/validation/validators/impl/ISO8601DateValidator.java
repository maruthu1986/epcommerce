/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.impl;

import java.time.format.DateTimeFormatter;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.validation.constraints.ISO8601DateConstraint;

/**
 * Date validator following ISO8061 standards. Can be used by any JSR-303/349 validation framework.
 * The String value being validated is valid only if actual value can be parsed into
 * {@link DateTimeFormatter#ISO_LOCAL_DATE} date value.
 */
public class ISO8601DateValidator implements ConstraintValidator<ISO8601DateConstraint, String> {

	@Override
	public void initialize(final ISO8601DateConstraint constraintAnnotation) {
		//nothing
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {

		if (StringUtils.isBlank(value)) {
			return true;
		}

		try {
			DateTimeFormatter.ISO_LOCAL_DATE.parse(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
