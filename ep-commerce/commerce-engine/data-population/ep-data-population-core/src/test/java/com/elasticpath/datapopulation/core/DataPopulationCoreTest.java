/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.datapopulation.core.context.configurer.FilterActionConfiguration;
import com.elasticpath.datapopulation.core.exceptions.SqlActionException;

/**
 * Tests the data population core main services: reset-db, filter-data will not fail when given the correct resources.
 */
public class DataPopulationCoreTest extends AbstractDataPopulationTestSetup {

	/**
	 * The name of the 'reset database' command as accessed by the Spring Shell Command Line Interface.
	 */
	protected static final String RESET_DATABASE_CLI_COMMAND = "reset-db";

	/**
	 * The name of the 'filter-data' command as accessed by the Spring Shell Command Line Interface.
	 */
	protected static final String FILTER_DATA_CLI_COMMAND = "filter-data";

	private static final String INVALID_PASSWORD = "invalidPassword";

	private static final String INVALID_URL = "invalidUrl";

	private static final String PASSWORD_KEY = "data.population.password";

	private static final String URL_KEY = "data.population.createdb.url";

	private static final String JDBC_KEY = "data.population.jdbc.driver";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@After
	public void deleteTemporaryDirectories() throws IOException{
		FileUtils.deleteDirectory(getWorkingDirectory());
		FileUtils.deleteDirectory(getOutputDirectory());
	}

	@Test
	public void testFilterDataActionWillRun() throws IOException {
		FilterActionConfiguration actionConfigurer = new FilterActionConfiguration();
		actionConfigurer.setFilterOutputDirectory(getOutputDirectory());
		getCore().getDataPopulationContext().setActionConfiguration(actionConfigurer);
		getCore().runActionExecutor(FILTER_DATA_CLI_COMMAND);
	}

	@Test
	public void testResetDatabaseActionWillRun() throws IOException {
		getCore().runActionExecutor(RESET_DATABASE_CLI_COMMAND);
	}

	@Test
	public void testInvalidCredentialShouldReturnAnException() throws IOException {
		DataPopulationCore dataPopulationCore = getCore();
		Properties properties = dataPopulationCore.getDatabaseConnectionProperties().getProperties();
		properties.setProperty(PASSWORD_KEY, INVALID_PASSWORD);

		expectSqlActionExceptionWithMessage("Cannot create PoolableConnectionFactory (invalid authorization specification)", dataPopulationCore);
	}

	@Test
	public void testInvalidUrlShouldReturnAnException() throws IOException {
		DataPopulationCore dataPopulationCore = getCore();
		Properties properties = dataPopulationCore.getDatabaseConnectionProperties().getProperties();
		properties.setProperty(URL_KEY, INVALID_URL);

		expectSqlActionExceptionWithMessage(
				String.format("Cannot create JDBC driver of class '%s' for connect URL '%s'", properties.getProperty(JDBC_KEY), INVALID_URL),
				dataPopulationCore);
	}

	private void expectSqlActionExceptionWithMessage(final String message, final DataPopulationCore dataPopulationCore) throws IOException {
		expectedException.expect(SqlActionException.class);
		expectedException.expectMessage(message);

		dataPopulationCore.runActionExecutor(RESET_DATABASE_CLI_COMMAND);
	}
}
