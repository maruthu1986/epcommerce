/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Test;

import com.elasticpath.datapopulation.core.AbstractDataPopulationTestSetup;

/**
 * Tests the database connection properties helper methods.
 */
public class DatabaseConnectionPropertiesTest extends AbstractDataPopulationTestSetup {

	public static final String DATA_POPULATION_JDBC_DRIVER = "data.population.jdbc.driver";

	DatabaseConnectionProperties dbConnProperties = new DatabaseConnectionProperties();

	@Test
	public void testFindProperty() {
		Properties properties = new Properties();

		properties.setProperty("keyOneFromSetOne", "valueOne");
		properties.setProperty("keyTwoFromSetTwo", "valueTwo");
		properties.setProperty("keyThreeFromSetTwo", "valueThree");

		String valueOne = dbConnProperties.findProperty("keyOneFromSetOne", properties);
		String valueTwo = dbConnProperties.findProperty("keyTwoFromSetTwo", properties);
		String valueThree = dbConnProperties.findProperty("keyThreeFromSetTwo", properties);

		assertThat(valueOne)
				.as("Find Property method did not find keyOneFromSetOne")
				.isNotNull();
		assertThat(valueTwo)
				.as("Find Property method did not find keyTwoFromSetTwo")
				.isNotNull();
		assertThat(valueThree)
				.as("Find Property method did not find keyThreeFromSetTwo")
				.isNotNull();
	}

	@Test
	public void testSpecifiedOverrideDatabaseType() {
		Properties properties = new Properties();
		properties.setProperty(DatabaseConnectionProperties.DATA_POPULATION_DATABASE_TYPE_KEY, "sqlserver");
		properties.setProperty(DATA_POPULATION_JDBC_DRIVER, "oracle.jdbc.driver.OracleDriver");

		dbConnProperties.getDatabaseTypeProperties(properties);

		String dbType = properties.getProperty(DatabaseConnectionProperties.DATA_POPULATION_DATABASE_TYPE_KEY);

		assertThat(dbType)
				.as("The database type is sqlserver has because the override")
				.isEqualTo("sqlserver");
	}
}
