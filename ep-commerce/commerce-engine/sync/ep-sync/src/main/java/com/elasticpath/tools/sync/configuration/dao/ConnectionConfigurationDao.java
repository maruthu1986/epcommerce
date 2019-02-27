/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.configuration.dao;

import java.net.URL;

import com.elasticpath.tools.sync.configuration.ConnectionConfiguration;

/**
 * A DAO for loading connection configurations.
 */
public interface ConnectionConfigurationDao {

	/**
	 * Loads a connection configuration by its configuration ID.
	 * 
	 * @param configurationLocation the configuration ID
	 * @return the connection configuration
	 */
	ConnectionConfiguration load(URL configurationLocation);

}