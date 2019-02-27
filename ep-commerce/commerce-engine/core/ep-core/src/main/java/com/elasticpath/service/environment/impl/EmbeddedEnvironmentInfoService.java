/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.environment.impl;

import com.elasticpath.service.environment.EnvironmentInfoService;

/**
 * Service for determining information about current running environment paths.
 */
public class EmbeddedEnvironmentInfoService implements EnvironmentInfoService {

	private static final String CURRENT_DIR = ".";
	private String applicationRootPath = CURRENT_DIR;
	private String configurationRootPath = CURRENT_DIR;

	@Override
	public String getApplicationRootPath() {
		return applicationRootPath;
	}

	@Override
	public String getConfigurationRootPath() {
		return configurationRootPath;
	}

	/**
	 * Sets the current application path.
	 * @param applicationPath The application path.
	 */
	public void setApplicationRootPath(final String applicationPath) {
		this.applicationRootPath = applicationPath;
	}

	/**
	 * Sets the configuration root directory path.
	 * @param configurationPath The configuration path.
	 */
	public void setConfigurationRootPath(final String configurationPath) {
		this.configurationRootPath = configurationPath;
	}
}