/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.validation.domain.impl;

import java.util.Locale;

import com.elasticpath.validation.domain.ValidationError;

/**
 * Validation error for spring expressions.
 */
public class SpringExpressionValidationError implements ValidationError {

	private String message;

	/**
	 * Constructor.
	 * @param message message
	 */
	public SpringExpressionValidationError(final String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getMessage(final Locale locale) {
		return getMessage();
	}

	public void setMessage(final String message) {
		this.message = message;
	}
}
