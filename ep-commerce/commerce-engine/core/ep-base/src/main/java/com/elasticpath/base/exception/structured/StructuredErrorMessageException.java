/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.base.exception.structured;

import java.util.Collection;

import com.elasticpath.base.common.dto.StructuredErrorMessage;

/**
 * Exception type which provides structured error messages.
 */
public interface StructuredErrorMessageException {

	/**
	 * Returns the structured error messages.
	 *
	 * @return StructuredErrorMessages the structured error messages
	 */
	Collection<StructuredErrorMessage> getStructuredErrorMessages();

	/**
	 * Returns the exception message.
	 *
	 * @return exception message
	 */
	String getMessage();
}
