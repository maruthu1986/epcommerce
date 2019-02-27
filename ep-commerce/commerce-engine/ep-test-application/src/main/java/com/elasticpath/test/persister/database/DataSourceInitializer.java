/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.persister.database;

import java.sql.SQLException;

/**
 * Interface for the simple persistent test scenarios that force a clean test DB schema once for ALL (not each)
 * test method within the test in which DataSourceService is used.
 */
public interface DataSourceInitializer extends DataSourceConfiguration {

	void dropAndCreateDatabase();

	void initializeSnapshot();

	String resetDatabase() throws SQLException;
}