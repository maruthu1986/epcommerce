/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusCheckerTarget;
import com.elasticpath.health.monitoring.StatusType;

/**
 * Base class for performing status checks.
 */
public abstract class AbstractStatusCheckerTarget implements StatusCheckerTarget {

	private String name;

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Instantiates a Status object and sets the values on it.
	 * 
	 * @param info the info
	 * @param type the type
	 * @param message the message
	 * @return the status
	 */
	protected Status createStatus(final StatusType type, final String message, final String info) {
		Status status = new StatusImpl();
		status.setStatus(type);
		status.setMessage(message);
		status.setInfo(info);
		return status;
	}

	/**
	 * Instantiates a {@link Status} with a status type of {@link StatusType#OK OK}.
	 * 
	 * @return an instantiated {@link Status} with a status type of {@link StatusType#OK OK}
	 */
	protected Status createOk() {
		return createStatus(StatusType.OK, null, null);
	}

}
