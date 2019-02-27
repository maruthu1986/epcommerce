/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring;

/**
 * @author dpendery
 */
public enum StatusType {
	/**
	 * OK.
	 */
	OK,

	/**
	 * Warning.
	 */
	WARNING,

	/**
	 * Unknown.
	 */
	UNKNOWN,

	/**
	 * Failure.
	 */
	CRITICAL
}
