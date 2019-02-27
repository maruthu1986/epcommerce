/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.base.exception.structured;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpSystemException;

/**
 * Ep structure error message exception represents an error in input data and contains a structured error
 * message to give detailed information about the invalid input which caused the error.
 */
public class EpStructureErrorMessageException extends EpSystemException implements StructuredErrorMessageException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final String message;
	private final List<StructuredErrorMessage> structuredErrorMessageList;

	/**
	 * Default constructor.
	 *
	 * @param message                    the message
	 * @param structuredErrorMessageList a list of commerce message
	 */
	public EpStructureErrorMessageException(final String message, final List<StructuredErrorMessage> structuredErrorMessageList) {
		super(message);
		this.message = message;
		this.structuredErrorMessageList = structuredErrorMessageList == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessageList);
	}

	/**
	 * Constructor.
	 *
	 * @param message                    the message
	 * @param structuredErrorMessageList a list of commerce message
	 * @param cause                      the cause
	 */
	public EpStructureErrorMessageException(final String message,
											final List<StructuredErrorMessage> structuredErrorMessageList,
											final Throwable cause) {
		super(message, cause);
		this.message = message;
		this.structuredErrorMessageList = structuredErrorMessageList == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessageList);
	}

	@Override
	public List<StructuredErrorMessage> getStructuredErrorMessages() {
		return structuredErrorMessageList;
	}

	@Override
	public String getMessage() {
		return message + ": " + structuredErrorMessageList.stream()
				.map(structuredErrorMessage -> "[" + structuredErrorMessage.toString() + "]")
				.collect(Collectors.joining(", "));
	}
}
