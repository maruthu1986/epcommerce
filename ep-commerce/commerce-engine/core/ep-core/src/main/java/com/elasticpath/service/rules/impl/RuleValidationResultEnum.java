/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.rules.impl;

/**
 * Rule validation enum.
 */
public enum RuleValidationResultEnum {

	/**
	 * Success.
	 */
	SUCCESS(true),
	/**
	 * Date not within range.
	 */
	ERROR_EXPIRED(false),
	/**
	 * Unspecified error.
	 */
	ERROR_UNSPECIFIED(false);

	private boolean success;

	/**
	 * Default private constructor.
	 * @param success whether or not this value is a success
	 */
	RuleValidationResultEnum(final boolean success) {
		this.success = success;
	}

	/**
	 * Determine if the value represents a success.
	 * @return is success flag.
	 */
	public boolean isSuccess() {
		return success;
	}
}
