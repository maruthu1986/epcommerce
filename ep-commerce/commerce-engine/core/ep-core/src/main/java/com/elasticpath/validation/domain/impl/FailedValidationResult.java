/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.validation.domain.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.elasticpath.validation.domain.ValidationConstraint;
import com.elasticpath.validation.domain.ValidationError;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * A failed validation result object.
 */
public class FailedValidationResult implements ValidationResult {

	private final ValidationConstraint validationConstraint;
	private final List<?> validationErrors;

	private String cachedRawErrorMessage;
	private Collection<ValidationError> cachedErrors;

	/**
	 * Constructor.
	 * @param constraint	constraint
	 * @param errors		error
	 */
	public FailedValidationResult(final ValidationConstraint constraint,
						   final List<?> errors) {
		validationConstraint = constraint;
		validationErrors = errors;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public String getMessage() {
		if (cachedRawErrorMessage == null) {
			cacheRawMessage();
		}
		return cachedRawErrorMessage;
	}

	private void cacheRawMessage() {
		cachedRawErrorMessage = validationErrors.stream()
				.map(Object::toString)
				.collect(Collectors.joining(", "));
	}

	@Override
	public String getMessage(final Locale locale) {
		return validationConstraint.getLocalizedErrorMessage(locale);
	}

	@Override
	public String toString() {
		return getMessage();
	}

	@Override
	public ValidationError[] getErrors() {
		if (cachedErrors == null) {
			cacheErrorsCollection();
		}
		return cachedErrors.toArray(new ValidationError[cachedErrors.size()]);
	}

	private void cacheErrorsCollection() {
		cachedErrors = new ArrayList<>(validationErrors.size());
		for (Object error : validationErrors) {
			cachedErrors.add(new SpringExpressionValidationError(error.toString()));
		}
	}

}