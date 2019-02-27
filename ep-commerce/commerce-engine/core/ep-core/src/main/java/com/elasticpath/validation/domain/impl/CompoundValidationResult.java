/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.validation.domain.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.validation.domain.ValidationError;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * Provides implementation of the validation result interface that enables to
 * create a compound result of the validation when a whole condition tree is validated.
 */
public class CompoundValidationResult implements ValidationResult {

	private boolean validResult = true;
	private final Collection<ValidationResult> results = new LinkedList<>();
	private Collection<ValidationError> cachedErrors;

	@Override
	public ValidationError[] getErrors() {
		if (cachedErrors == null) {
			cacheErrorsCollection();
		}

		return cachedErrors.toArray(new ValidationError[cachedErrors.size()]);
	}

	private void cacheErrorsCollection() {

		cachedErrors = new ArrayList<>(results.size());
		for (ValidationResult result : results) {
			if (!result.isValid()) {
				cachedErrors.add(new CompoundValidationError(result));
			}

		}
	}

	@Override
	public String getMessage() {
		return accumulateMessagesFromErrors(null);
	}

	@Override
	public String getMessage(final Locale locale) {
		return accumulateMessagesFromErrors(locale);
	}

	/**
	 * @return list of error messages from errors array separated by a new line character
	 */
	private String accumulateMessagesFromErrors(final Locale locale) {
		if (this.getErrors().length > 0) {
			return Arrays.stream(getErrors())
					.map(error -> locale == null ? error.getMessage() : error.getMessage(locale))
					.collect(Collectors.joining("\n"));
		}
		return StringUtils.EMPTY;
	}

	@Override
	public boolean isValid() {
		return validResult;
	}

	/**
	 * Appends the validation result to the collection of the validation results and
	 * updates the validResult property depending on the result being added. If any of the appended
	 * results is invalid then the whole compound result is invalid also.
	 * @param result the result of the validation of a single condition.
	 */
	public void addValidationResult(final ValidationResult result) {
		results.add(result);
		if (isValid()) {
			this.validResult = result.isValid();
		}
	}

}
