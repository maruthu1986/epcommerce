/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.validation.domain.impl;

import java.util.Locale;

import com.elasticpath.validation.domain.ValidationError;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * Validation error for compound conditions.
 */
public class CompoundValidationError implements ValidationError {

	private ValidationResult validationResult;

	/**
	 * Constructor.
	 * @param validationResult validation result
	 */
	public CompoundValidationError(final ValidationResult validationResult) {
		this.validationResult = validationResult;
	}

	@Override
	public String getMessage() {
		return validationResult.getMessage();
	}

	@Override
	public String getMessage(final Locale locale) {
		return validationResult.getMessage(locale);
	}

	public void setValidationResult(final ValidationResult validationResult) {
		this.validationResult = validationResult;
	}

	public ValidationResult getValidationResult() {
		return validationResult;
	}
}
