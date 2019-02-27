/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation;

import java.util.Collection;

import com.elasticpath.base.common.dto.StructuredErrorMessage;

/**
 * Base interface for validators.
 * @param <T> context type
 */
public interface Validator<T> {

	/**
	 * Validates the object.
	 * @param context object to be validated.
	 * @return a collection of Structured Error Messages containing validation errors, or an
	 * 			empty collection if the validation is successful.
	 */
	Collection<StructuredErrorMessage> validate(T context);
}
