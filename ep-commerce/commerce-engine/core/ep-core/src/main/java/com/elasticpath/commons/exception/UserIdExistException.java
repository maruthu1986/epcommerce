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
 * The exception for userId address already exists.
 *
 * @author wliu
 */
public class UserIdExistException extends EpSystemException implements InvalidBusinessStateException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final List<StructuredErrorMessage> structuredErrorMessages;

	/**
	 * Creates a new <code>UserIdExistException</code> object with the given message.
	 *
	 * @param message the reason for this <code>UserIdExistException</code>.
	 * @deprecated use {@link #UserIdExistException(String, Collection<StructuredErrorMessage>)} instead.
	 */
	@Deprecated
	public UserIdExistException(final String message) {
		super(message);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * Creates a new <code>UserIdExistException</code> object with the given message.
	 *
	 * @param message                 the reason for this <code>UserIdExistException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>UserIdExistException</code>.
	 */
	public UserIdExistException(final String message, final Collection<StructuredErrorMessage> structuredErrorMessages) {
		super(message);
		this.structuredErrorMessages = structuredErrorMessages == null ? emptyList() : ImmutableList.copyOf(structuredErrorMessages);
	}

	/**
	 * Creates a new <code>UserIdExistException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>UserIdExistException</code>.
	 * @param cause   the <code>Throwable</code> that caused this <code>UserIdExistException</code>.
	 * @deprecated use {@link #UserIdExistException(String, Collection<StructuredErrorMessage>, Throwable)} instead.
	 */
	@Deprecated
	public UserIdExistException(final String message, final Throwable cause) {
		super(message, cause);
		structuredErrorMessages = asList(new StructuredErrorMessage(null, message, null));
	}

	/**
	 * Creates a new <code>UserIdExistException</code> object using the given message and cause exception.
	 *
	 * @param message                 the reason for this <code>UserIdExistException</code>.
	 * @param structuredErrorMessages the detailed reason for this <code>UserIdExistException</code>.
	 * @param cause                   the <code>Throwable</code> that caused this <code>UserIdExistException</code>.
	 */
	public UserIdExistException(
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