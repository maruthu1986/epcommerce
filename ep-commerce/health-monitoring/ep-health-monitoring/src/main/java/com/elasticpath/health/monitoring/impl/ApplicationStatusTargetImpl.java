/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusCheckerTarget;
import com.elasticpath.health.monitoring.StatusType;

/**
 * Checks the application status by getting the application name.
 */
public class ApplicationStatusTargetImpl extends AbstractStatusCheckerTarget implements ApplicationContextAware, StatusCheckerTarget {

	private static final Logger LOG = Logger.getLogger(ApplicationStatusTargetImpl.class);

	private ApplicationContext applicationContext;

	/**
	 * Checks the application status by retrieving the CM Email URL setting value.
	 * 
	 * @return OK if the setting could be retrieved
	 * @see com.elasticpath.health.monitoring.impl.AbstractStatusCheckerTarget#check()
	 */
	@Override
	public Status check() {
		Status status;

		LOG.debug("Checking application status.");
		try {
			String applicationName = String.valueOf(applicationContext.getBean("applicationName"));
			status = createStatus(StatusType.OK, "Application is up", "Application name = " + applicationName);
		} catch (Exception exn) {
			LOG.error("Failed application status.", exn);
			status = createStatus(StatusType.CRITICAL, "Exception", exn.getMessage());
		}

		return status;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
