/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.repo.ext.health.monitor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;
import com.elasticpath.health.monitoring.impl.ApplicationStatusTargetImpl;
import com.elasticpath.health.monitoring.impl.HttpStatusTargetImpl;

/**
 * Extension class that overrides the default behaviour of ApplicationStatusTargetImpl.
 */
public class ExtApplicationStatusTargetImpl extends ApplicationStatusTargetImpl {

	private static final Logger LOG = Logger.getLogger(ExtApplicationStatusTargetImpl.class);

	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * Checks the application status by checking the cortex healthcheck url.
	 *
	 * @return OK if the setting could be retrieved
	 * @see com.elasticpath.health.monitoring.impl.AbstractStatusCheckerTarget#check()
	 */
	@Override
	public Status check() {
		Status status;
		try {
			String applicationName = String.valueOf(applicationContext.getBean("applicationName"));
			HttpStatusTargetImpl httpStatusTarget = new HttpStatusTargetImpl();
			httpStatusTarget.setName(applicationName);
			httpStatusTarget.setUrl(ApplicationUrlHelper.getHealthCheckUrl());
			status = httpStatusTarget.check();
		} catch (Exception exn) {
			LOG.error("Failed application status.", exn);
			status = createStatus(StatusType.CRITICAL, "Exception", exn.getMessage());
		}

		return status;
	}

}
