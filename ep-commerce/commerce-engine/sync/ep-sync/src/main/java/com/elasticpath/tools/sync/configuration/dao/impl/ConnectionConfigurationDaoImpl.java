/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.configuration.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.helpers.DefaultValidationEventHandler;

import com.elasticpath.tools.sync.configuration.ConnectionConfiguration;
import com.elasticpath.tools.sync.configuration.dao.ConnectionConfigurationDao;
import com.elasticpath.tools.sync.configuration.marshal.XMLUnmarshaller;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * A DAO for loading connection configurations.
 */
public class ConnectionConfigurationDaoImpl implements ConnectionConfigurationDao {

	private static final String CONNECTION_CONFIG_SCHEMA = "schema/connectionSchema.xsd";

	/**
	 * Loads a connection configuration.
	 *
	 * @param configurationLocation the configuration location
	 * @return the connection configuration
	 */
	@Override
	public ConnectionConfiguration load(final URL configurationLocation) {
		try (InputStream inputStream = configurationLocation.openStream()) {
			return createUnmarshaller().unmarshall(inputStream);
		} catch (final IOException e) {
			throw new SyncToolConfigurationException("Could not load connection configuration", e);
		}
	}

	/**
	 * Creates XMLUnmarshaller.
	 *
	 * @return XMLUnmarshaller
	 */
	protected XMLUnmarshaller createUnmarshaller() {
		final XMLUnmarshaller unmarshaller = new XMLUnmarshaller(ConnectionConfiguration.class);
		final ValidationEventHandler validationEventHandler = new DefaultValidationEventHandler();
		unmarshaller.initValidationParameters(CONNECTION_CONFIG_SCHEMA, validationEventHandler);
		return unmarshaller;
	}

}
