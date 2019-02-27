/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.core.convert.ConversionService;

import com.elasticpath.base.exception.structured.EpValidationException;
import com.elasticpath.base.exception.structured.StructuredErrorMessageException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.commons.exception.UnavailableException;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorMessageTransformer;

/**
 * The exception transformer impl.
 */
@Singleton
@Named("exceptionTransformer")
public class ExceptionTransformerImpl implements ExceptionTransformer {

	private final ConversionService conversionService;
	private final StructuredErrorMessageTransformer structuredErrorMessageTransformer;

	/**
	 * Constructor.
	 *
	 * @param conversionService                 the conversion service
	 * @param structuredErrorMessageTransformer the structured error message transformer
	 */
	@Inject
	ExceptionTransformerImpl(
			@Named("conversionService")
			final ConversionService conversionService,
			@Named("structuredErrorMessageTransformer")
			final StructuredErrorMessageTransformer structuredErrorMessageTransformer) {
		this.conversionService = conversionService;
		this.structuredErrorMessageTransformer = structuredErrorMessageTransformer;
	}

	@Override
	public ResourceOperationFailure getResourceOperationFailure(final EpValidationException error) {
		return ResourceOperationFailure.badRequestBody(error.getMessage(), getMessages(error), error);
	}

	@Override
	public ResourceOperationFailure getResourceOperationFailure(final InvalidBusinessStateException error) {
		return ResourceOperationFailure.stateFailure(error.getMessage(), getMessages(error), (Throwable) error);
	}

	@Override
	public ResourceOperationFailure getResourceOperationFailure(final UnavailableException error) {
		return ResourceOperationFailure.notFound(error.getMessage(), getMessages(error), (Throwable) error);
	}

	@Override
	public <T> ExecutionResult<T> getExecutionResult(final EpValidationException error) {
		return ExecutionResultFactory.createBadRequestBodyWithMessages(error.getMessage(), getMessages(error));
	}

	@Override
	public <T> ExecutionResult<T> getExecutionResult(final InvalidBusinessStateException error) {
		return ExecutionResultFactory.createStateFailureWithMessages(error.getMessage(), getMessages(error));
	}

	@Override
	public List<Message> getMessages(final StructuredErrorMessageException error) {
		return error.getStructuredErrorMessages()
				.stream()
				.map(sem -> conversionService.convert(sem, Message.class))
				.collect(Collectors.toList());
	}

	private List<Message> getMessages(final EpValidationException error) {
		return structuredErrorMessageTransformer.transform(error.getStructuredErrorMessages(), null);
	}

}
