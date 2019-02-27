/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring;

import java.util.Map;

/**
 * Checks the status of the system using a set of {@link StatusCheckerTarget}s.
 */
public interface StatusChecker {

	/**
	 * Checks the various status and returns a map of the results.
	 *
	 * @return map of statuses.
	 */
	Map<String, Status> checkStatus();

	/**
	 * Checks a subset of targets.  Intended for quick checks by load balancers.
	 *
	 * @return map of statuses.
	 */
	Map<String, Status> checkStatusSimple();

}
