/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform;

import java.util.List;

import com.elasticpath.base.exception.structured.EpValidationException;
import com.elasticpath.base.exception.structured.StructuredErrorMessageException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.commons.exception.UnavailableException;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The exception transformer.
 */
public interface ExceptionTransformer {

	/**
	 * Transform the {@link EpValidationException} to appropriate {@link ResourceOperationFailure}.
	 *
	 * @param error the exception to transform
	 * @return the resource operation failure
	 */
	ResourceOperationFailure getResourceOperationFailure(EpValidationException error);

	/**
	 * Transform the {@link InvalidBusinessStateException} to appropriate {@link ResourceOperationFailure}.
	 *
	 * @param error the exception to transform
	 * @return the resource operation failure
	 */
	ResourceOperationFailure getResourceOperationFailure(InvalidBusinessStateException error);

	/**
	 * Transform the {@link UnavailableException} to appropriate {@link ResourceOperationFailure}.
	 *
	 * @param error the exception to transform
	 * @return the resource operation failure
	 */
	ResourceOperationFailure getResourceOperationFailure(UnavailableException error);

	/**
	 * Transform the {@link EpValidationException} to appropriate {@link ExecutionResult}.
	 *
	 * @param error the exception to transform
	 * @param <T>   the Type of ExecutionResult
	 * @return the execution result
	 */
	<T> ExecutionResult<T> getExecutionResult(EpValidationException error);

	/**
	 * Transform the {@link InvalidBusinessStateException} to appropriate {@link ExecutionResult}.
	 *
	 * @param error the exception to transform
	 * @param <T>   the Type of ExecutionResult
	 * @return the execution result
	 */
	<T> ExecutionResult<T> getExecutionResult(InvalidBusinessStateException error);

	/**
	 * Get a list of {@link Message} from a list of {@link com.elasticpath.base.common.dto.StructuredErrorMessage}.
	 *
	 * @param error the StructuredErrorMessageException
	 * @return messages
	 */
	List<Message> getMessages(StructuredErrorMessageException error);
}
