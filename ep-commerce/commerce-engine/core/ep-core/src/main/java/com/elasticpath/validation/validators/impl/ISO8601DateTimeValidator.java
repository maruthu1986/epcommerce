/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.impl;

import java.time.format.DateTimeFormatter;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.validation.constraints.ISO8601DateTimeConstraint;

/**
 * Date/Time validator following ISO8061 standards. Can be used by any JSR-303/349 validation framework.
 * The String value being validated is valid only if actual value can be parsed into
 * {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME} date-time value.
 */
public class ISO8601DateTimeValidator implements ConstraintValidator<ISO8601DateTimeConstraint, String> {

	@Override
	public void initialize(final ISO8601DateTimeConstraint constraintAnnotation) {
		//nothing
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {

		if (StringUtils.isBlank(value)) {
			return true;
		}

		try {
			DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
