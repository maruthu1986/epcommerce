/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException;
import com.elasticpath.datapopulation.core.service.filtering.FilteredPropertiesFactory;
import com.elasticpath.datapopulation.core.utils.ClasspathResourceResolverUtil;

/**
 * The class representing a set of database connection properties.
 */
public class DatabaseConnectionProperties {

	/**
	 * The property key for database type.
	 */
	public static final String DATA_POPULATION_DATABASE_TYPE_KEY = "data.population.database.type";
	private final Properties dbConnectionProperties = new Properties();

	@Autowired
	private ClasspathResourceResolverUtil classpathResolver;

	private List<String> candidateDatabaseTypes;

	@Autowired
	@Qualifier("databaseProperties")
	private Properties databaseProperties;

	/**
	 * Initializes the database connection properties using the database.properties file from the environment directory.
	 */
	public void initialize() {
		dbConnectionProperties.clear();
		dbConnectionProperties.putAll(databaseProperties);
		getDatabaseTypeProperties(dbConnectionProperties);
		dbConnectionProperties.putAll(getDatabaseConnectionUrls(dbConnectionProperties,
				findProperty(DATA_POPULATION_DATABASE_TYPE_KEY, dbConnectionProperties)));
	}

	/**
	 * Calls FilterPropertiesFactory to load the property files and then filter the placeholder values.
	 * Returns the database connections in use properties.
	 *
	 * @param sourceProperties the source properties used to filter placeholders in the
	 *                         {@link FilteredPropertiesFactory}
	 * @param databaseType     the database type
	 * @return databaseConnectionsInUserProperties the db connection properties
	 */
	protected Properties getDatabaseConnectionUrls(final Properties sourceProperties, final String databaseType) {
		InputStream unfilteredUrlProperties = classpathResolver.getFileResourceStreamWithFallback(databaseType, "database-url/%s.properties");
		if (unfilteredUrlProperties == null) {
			throw new DataPopulationActionException("The database properties file for " + databaseType
					+ " cannot be found on classpath");
		}

		Properties dbUrlsProperties = new Properties();
		try {
			dbUrlsProperties.load(unfilteredUrlProperties);
		} catch (IOException e) {
			throw new DataPopulationActionException("The properties file cannot be read for database type: " + databaseType, e);
		}

		FilteredPropertiesFactory filterPropertiesFactory = new FilteredPropertiesFactory();
		filterPropertiesFactory.setSourceProperties(sourceProperties);
		filterPropertiesFactory.setPropertiesToFilter(dbUrlsProperties);

		return extractFilterFactoryProperties(filterPropertiesFactory);
	}

	/**
	 * Returns the database vendor used for data population (MySql, Oracle, ...).
	 * <p>
	 * It first checks to see if there is database type already specified in the database.properties configuration file.
	 * If it exists, this method does nothing.
	 * <p>
	 * If not, the default action is to calculate the database type by finding a match between the jdbc driver's name and the
	 * list of known driver types (spring configured). It will throw an exception if no matches are found.
	 *
	 * @param properties properties containing the jdbc override
	 */
	protected void getDatabaseTypeProperties(final Properties properties) {
		final String dbType = properties.getProperty(DATA_POPULATION_DATABASE_TYPE_KEY);
		if (StringUtils.isEmpty(dbType)) {
			String driverProp = properties.getProperty("data.population.jdbc.driver"); // determine the connection type from the jdbc driver
			if (StringUtils.isEmpty(driverProp)) {
				throw new DataPopulationActionException("Cannot determine the database connection type "
						+ "because the jdbc database driver type is not set.");
			}

			driverProp = driverProp.toLowerCase(Locale.getDefault());

			String candidateDbType = getCandidateDatabaseTypes()
					.stream()
					.filter(driverProp::contains)
					.findFirst()
					.orElseThrow(() -> new DataPopulationActionException("Cannot determine the database connection type "
							+ "because the jdbc database driver type is not known"));

			properties.setProperty(DATA_POPULATION_DATABASE_TYPE_KEY, candidateDbType);
		}
	}

	private Properties extractFilterFactoryProperties(final FilteredPropertiesFactory filterPropertiesFactory) {
		Properties property;
		try {
			property = filterPropertiesFactory.getObject();
		} catch (IOException ex) {
			throw new DataPopulationActionException("Error creating the database connection by type properties", ex);
		}
		return property;
	}

	/**
	 * Gets the database properties for database connection setup.
	 *
	 * @return databaseProperties the properties
	 */
	public Properties getProperties() {
		return dbConnectionProperties;
	}

	public String getDataSourceUrl() {
		return findProperty("data.population.url", dbConnectionProperties);
	}

	public String getDataSourceUsername() {
		return findProperty("data.population.username", dbConnectionProperties);
	}

	public String getDataSourcePassword() {
		return findProperty("data.population.password", dbConnectionProperties);
	}

	public String getDataSourceDriverName() {
		return findProperty("data.population.jdbc.driver", dbConnectionProperties);
	}

	public String getCreateDataSourceUrl() {
		return findProperty("data.population.createdb.url", dbConnectionProperties);
	}

	public String getCreateDataSourceUsername() {
		return findProperty("data.population.reset_user", dbConnectionProperties);
	}

	public String getCreateDataSourcePassword() {
		return findProperty("data.population.reset_password", dbConnectionProperties);
	}

	public String getCreateDataSourceDriverName() {
		return findProperty("data.population.jdbc.driver", dbConnectionProperties);
	}

	public List<String> getCandidateDatabaseTypes() {
		return candidateDatabaseTypes;
	}

	public void setCandidateDatabaseTypes(final List<String> candidateDatabaseTypes) {
		this.candidateDatabaseTypes = candidateDatabaseTypes;
	}

	/**
	 * Finds the property value from a list of properties. Because the properties are unsorted, it does a linear search.
	 * Do not repeat keys in the list. It will return the last key match found.
	 *
	 * @param key        the key
	 * @param properties the properties
	 * @return propertyValue, will throw an exception if the value cannot be found
	 */
	protected String findProperty(final String key, final Properties properties) {
		return findProperty(key, properties, true);
	}

	private String findProperty(final String key, final Properties properties, final boolean failFast) {
		String propertyValue = null;
		if (properties.getProperty(key) != null) {
			propertyValue = properties.getProperty(key);
		}

		if (propertyValue == null && failFast) {
			throw new DataPopulationActionException("Cannot find the database property value of " + key);
		}
		return propertyValue;
	}
}
