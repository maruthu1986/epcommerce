/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.exceptions;

import static java.util.Collections.emptyList;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.structured.EpStructureErrorMessageException;

/**
 * This exception is thrown when processing shipping options.
 */
public class ShippingOptionServiceException extends EpStructureErrorMessageException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final List<StructuredErrorMessage> structuredErrorMessages;

	/**
	 * Constructor with exception message, structured messages and throwable.
	 *
	 * @param message                 the reason for this exception
	 * @param structuredErrorMessages the detailed reason for this exception
	 * @param cause                   the cause of this exception
	 */
	public ShippingOptionServiceException(
			final String message,
			final List<StructuredErrorMessage> structuredErrorMessages,
			final Throwable cause) {
		super(message, structuredErrorMessages, cause);
		this.structuredErrorMessages = structuredErrorMessages == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessages);
	}

	@Override
	public List<StructuredErrorMessage> getStructuredErrorMessages() {
		return structuredErrorMessages;
	}
}
