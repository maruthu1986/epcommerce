/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.base.exception.structured;

import java.util.Collection;
import java.util.stream.Collectors;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpSystemException;

/**
 * Validation exception represents an error in input data and contains a structured error
 * message to give detailed information about the invalid input which caused the error.
 */
public class EpValidationException extends EpSystemException implements StructuredErrorMessageException {

	private static final long serialVersionUID = -8999932578270387947L;

	private final String message;
	private final Collection<StructuredErrorMessage> structuredErrorMessageList;


	/**
	 * Default constructor.
	 * @param message the message
	 * @param structuredErrorMessageList a list of commerce message
	 */
	public EpValidationException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessageList) {
		this(message, structuredErrorMessageList, null);
	}

	/**
	 * Constructor with cause value.
	 * @param message the message
	 * @param structuredErrorMessages a list of commerce message
	 * @param cause cause
	 */
	public EpValidationException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessages, final Throwable cause) {
		super(message, cause);
		this.message = message;
		this.structuredErrorMessageList = structuredErrorMessages;
	}

	@Override
	public Collection<StructuredErrorMessage> getStructuredErrorMessages() {
		return structuredErrorMessageList;
	}

	@Override
	public String getMessage() {
		return message + ": " + structuredErrorMessageList.stream()
				.map(structuredErrorMessage -> "[" + structuredErrorMessage.toString() + "]")
				.collect(Collectors.joining(", "));
	}
}
