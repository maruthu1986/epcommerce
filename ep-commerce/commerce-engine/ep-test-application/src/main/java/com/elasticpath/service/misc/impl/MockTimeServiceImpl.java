/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.misc.impl;

import java.util.Date;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.TimeService;

/**
 * A Mock implementation of the TimeService for integration testing purposes.
 */
public class MockTimeServiceImpl implements TimeService {
	private Date currentTime;

	@Override
	public Date getCurrentTime() {
		if (currentTime == null) {
			throw new EpServiceException("MockTimeServiceImpl needs not be manually initialized with a hardcoded timestamp");
		}

		return currentTime;
	}

	public void setCurrentTime(final Date currentTime) {
		this.currentTime = currentTime;
	}
}
