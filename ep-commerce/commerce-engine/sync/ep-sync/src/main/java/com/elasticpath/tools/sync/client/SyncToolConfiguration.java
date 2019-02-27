/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client;

import com.elasticpath.tools.sync.configuration.ConnectionConfiguration;

/**
 * An interface holding information for the sync tool configuration parameters.
 */
public interface SyncToolConfiguration {

	/**
	 * Gets the target configuration name.
	 *
	 * @return the target config name
	 */
	ConnectionConfiguration getTargetConfig();

	/**
	 * Gets the source configuration name.
	 *
	 * @return the source config name
	 */
	ConnectionConfiguration getSourceConfig();

	/**
	 * Gets the requested controller type.
	 *
	 * @return the controller type
	 */
	SyncToolControllerType getControllerType();

}
