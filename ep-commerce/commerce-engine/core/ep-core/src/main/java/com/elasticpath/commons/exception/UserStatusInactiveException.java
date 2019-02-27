/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.commons.exception;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpSystemException;


/**
 * The exception for user inactive status.
 *
 * @author wliu
 */
public class UserStatusInactiveException extends EpSystemException implements InvalidBusinessStateException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final List<StructuredErrorMessage> structuredErrorMessages;

	/**
	 * Creates a new <code>UserStatusInactiveException</code> object with the given message.
	 *
	 * @param message the reason for this <code>UserStatusInactiveException</code>.
	 * @deprecated use {@link #UserStatusInactiveException(String, Collection <StructuredErrorMessage>)} instead.
	 */
	@Deprecated
	public UserStatusInactiveException(final String message) {
		super(message);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * The constructor.
	 *
	 * @param message                 the reason for this <code>UserStatusInactiveException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>UserStatusInactiveException</code>.
	 */
	public UserStatusInactiveException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessages) {
		super(message);
		this.structuredErrorMessages = structuredErrorMessages == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessages);
	}


	/**
	 * Creates a new <code>UserStatusInactiveException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>UserStatusInactiveException</code>.
	 * @param cause   the <code>Throwable</code> that caused this <code>UserStatusInactiveException</code>.
	 * @deprecated use {@link #UserStatusInactiveException(String, Collection<StructuredErrorMessage>, Throwable)} instead.
	 */
	@Deprecated
	public UserStatusInactiveException(final String message, final Throwable cause) {
		super(message, cause);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * Constructor with a throwable.
	 *
	 * @param message                 the reason for this <code>UserStatusInactiveException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>UserStatusInactiveException</code>.
	 * @param cause                   the <code>Throwable</code> that caused this <code>UserStatusInactiveException</code>.
	 */
	public UserStatusInactiveException(
			final String message,
			final Collection<StructuredErrorMessage> structuredErrorMessages,
			final Throwable cause) {
		super(message, cause);
		this.structuredErrorMessages = structuredErrorMessages == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessages);
	}


	@Override
	public List<StructuredErrorMessage> getStructuredErrorMessages() {
		return structuredErrorMessages;
	}

}