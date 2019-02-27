/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring;

/**
 * Status.
 */
public interface Status {

	/**
	 * Gets the status value.
	 *
	 * @return the status
	 */
	StatusType getStatus();

	/**
	 * @param status the status to set
	 */
	void setStatus(StatusType status);

	/**
	 * @return the info
	 */
	String getInfo();

	/**
	 * @param info the info to set
	 */
	void setInfo(String info);

	/**
	 * Sets the message.
	 *
	 * @param message the message
	 */
	void setMessage(String message);

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	String getMessage();

}