/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;

/**
 * Implementation of StatusCheckerTarget which is used to return an UNKNOWN status when no endpoint urls are configured.
 */
public class InvalidEndpointStatusTarget extends AbstractStatusCheckerTarget {

	@Override
	public Status check() {
		return createStatus(StatusType.UNKNOWN, "Error", "No endpoint URLs configured.");
	}
}
