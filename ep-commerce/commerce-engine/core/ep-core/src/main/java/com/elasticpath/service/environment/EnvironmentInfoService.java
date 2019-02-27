/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.environment;

/**
 * Service for determining information about current running environment paths.
 */
public interface EnvironmentInfoService {
	
	/**
	 * Gets the application path.
	 * @return the absolute directory path to root of the WAR
	 */
	String getApplicationRootPath();

	/**
	 * Return the absolute directory path to the root configuration of the application.
	 * @return the absolute configuration directory path.
	 */
	String getConfigurationRootPath();
}
