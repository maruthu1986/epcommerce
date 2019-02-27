/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring;

/**
 * Interface for validating a given response to make sure it is as expected.
 *
 * @param <T> the type of object this validator can validate.
 */
public interface ResponseValidator<T> {
	/**
	 * Validates the given response.
	 *
	 * @param response the response to validate.
	 * @return a {@link Status} object containing the validation result.
	 */
	Status validate(T response);
}
