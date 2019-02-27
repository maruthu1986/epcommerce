/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;


/**
 * This exception is thrown when a an unspecified error occurs
 * while processing the given card information.
 */
public class InsufficientInventoryException extends EpServiceException implements InvalidBusinessStateException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final List<StructuredErrorMessage> structuredErrorMessages;

	/**
	 * Constructor.
	 *
	 * @param message error message
	 */
	public InsufficientInventoryException(final String message) {
		super(message);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));

	}

	/**
	 * The constructor.
	 *
	 * @param message                 the reason for this <code>InsufficientInventoryException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>InsufficientInventoryException</code>.
	 */
	public InsufficientInventoryException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessages) {
		super(message);
		this.structuredErrorMessages = structuredErrorMessages == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessages);
	}

	@Override
	public List<StructuredErrorMessage> getStructuredErrorMessages() {
		return structuredErrorMessages;
	}

}
