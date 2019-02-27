package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.structured.EpValidationException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.commons.exception.UnavailableException;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorMessageTransformer;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionTransformerImplTest {

	private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

	@Mock
	private ConversionService conversionService;

	@Mock
	private StructuredErrorMessage structuredErrorMessage;

	@Mock
	private StructuredErrorMessageTransformer structuredErrorMessageTransformer;

	@Mock
	private Message message;

	@InjectMocks
	private ExceptionTransformerImpl exceptionTransformer;

	@Test
	public void getResourceOperationFailureWithUnavailableException() {
		// Given
		SampleUnavailableException error = new SampleUnavailableException(ERROR_MESSAGE);
		when(conversionService.convert(structuredErrorMessage, Message.class)).thenReturn(message);

		// When
		ResourceOperationFailure resourceOperationFailure = exceptionTransformer.getResourceOperationFailure(error);

		// Then
		assertEquals(ERROR_MESSAGE, resourceOperationFailure.getMessage());
		assertEquals(ResourceStatus.NOT_FOUND, resourceOperationFailure.getResourceStatus());
		assertEquals(1, resourceOperationFailure.getMessages().size());
		Message returnedMessage = resourceOperationFailure.getMessages().iterator().next();
		assertEquals(message, returnedMessage);
	}

	@Test
	public void getResourceOperationFailureWithInvalidBusinessStateException() {
		// Given
		SampleInvalidBusinessStateException error = new SampleInvalidBusinessStateException(ERROR_MESSAGE);
		when(conversionService.convert(structuredErrorMessage, Message.class)).thenReturn(message);

		// When
		ResourceOperationFailure resourceOperationFailure = exceptionTransformer.getResourceOperationFailure(error);

		// Then
		assertEquals(ERROR_MESSAGE, resourceOperationFailure.getMessage());
		assertEquals(ResourceStatus.STATE_FAILURE, resourceOperationFailure.getResourceStatus());
		assertEquals(1, resourceOperationFailure.getMessages().size());
		Message returnedMessage = resourceOperationFailure.getMessages().iterator().next();
		assertEquals(message, returnedMessage);
	}

	@Test
	public void getResourceOperationFailureWithEpValidationException() {
		// Given
		Set<StructuredErrorMessage> structuredErrorMessageList = Collections.singleton(structuredErrorMessage);
		EpValidationException error = new EpValidationException(ERROR_MESSAGE, structuredErrorMessageList);
		when(structuredErrorMessageTransformer.transform(structuredErrorMessageList, null)).thenReturn(Collections.singletonList(message));

		// When
		ResourceOperationFailure resourceOperationFailure = exceptionTransformer.getResourceOperationFailure(error);

		// Then
		assertEquals(ERROR_MESSAGE + ": [structuredErrorMessage]", resourceOperationFailure.getMessage());
		assertEquals(ResourceStatus.BAD_REQUEST_BODY, resourceOperationFailure.getResourceStatus());
		assertEquals(1, resourceOperationFailure.getMessages().size());
		Message returnedMessage = resourceOperationFailure.getMessages().iterator().next();
		assertEquals(message, returnedMessage);
	}

	@Test
	public void getExecutionResultWithEpValidationException() {
		// Given
		Set<StructuredErrorMessage> structuredErrorMessageList = Collections.singleton(structuredErrorMessage);
		EpValidationException error = new EpValidationException(ERROR_MESSAGE, structuredErrorMessageList);
		when(structuredErrorMessageTransformer.transform(structuredErrorMessageList, null)).thenReturn(Collections.singletonList(message));

		// When
		ExecutionResult<Object> executionResult = exceptionTransformer.getExecutionResult(error);

		// Then
		assertEquals(ERROR_MESSAGE + ": [structuredErrorMessage]", executionResult.getErrorMessage());
		assertEquals(ResourceStatus.BAD_REQUEST_BODY, executionResult.getResourceStatus());
		assertEquals(1, executionResult.getStructuredErrorMessages().size());
		Message returnedMessage = executionResult.getStructuredErrorMessages().iterator().next();
		assertEquals(message, returnedMessage);
	}

	@Test
	public void getExecutionResultWithInvalidBusinessStateException() {
		// Given
		SampleInvalidBusinessStateException error = new SampleInvalidBusinessStateException(ERROR_MESSAGE);
		when(conversionService.convert(structuredErrorMessage, Message.class)).thenReturn(message);

		// When
		ExecutionResult<Object> executionResult = exceptionTransformer.getExecutionResult(error);

		// Then
		assertEquals(ERROR_MESSAGE, executionResult.getErrorMessage());
		assertEquals(ResourceStatus.STATE_FAILURE, executionResult.getResourceStatus());
		assertEquals(1, executionResult.getStructuredErrorMessages().size());
		Message returnedMessage = executionResult.getStructuredErrorMessages().iterator().next();
		assertEquals(message, returnedMessage);
	}

	private class SampleUnavailableException extends Throwable implements UnavailableException {
		SampleUnavailableException(final String message) {
			super(message);
		}

		@Override
		public Collection<StructuredErrorMessage> getStructuredErrorMessages() {
			return Collections.singleton(structuredErrorMessage);
		}
	}

	private class SampleInvalidBusinessStateException extends Throwable implements InvalidBusinessStateException {
		SampleInvalidBusinessStateException(final String message) {
			super(message);
		}

		@Override
		public Collection<StructuredErrorMessage> getStructuredErrorMessages() {
			return Collections.singleton(structuredErrorMessage);
		}
	}
}