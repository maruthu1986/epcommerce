/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.action;

import java.sql.SQLException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.datapopulation.core.context.DatabaseConnectionProperties;
import com.elasticpath.datapopulation.core.exceptions.SqlActionException;

/**
 * An abstract action class which provides a data source based on the database connection properties configured.
 * Any action which requires access to a data source will inherit this.
 */
public abstract class AbstractDataSourceAccessAction implements DataPopulationAction {

	private static final Logger LOG = Logger.getLogger(AbstractDataSourceAccessAction.class);

	@Autowired
	@Qualifier("databaseConnectionProperties")
	private DatabaseConnectionProperties databaseConnectionProperties;

	/**
	 * Creates a data source with settings from database connection properties file which is environment specific.
	 *
	 * @param useCreateDbConnection if true use the create db profile
	 * @return dataSource the data source generated from the settings
	 */
	protected DataSource createDataSource(final boolean useCreateDbConnection) {
		if (useCreateDbConnection) {
			return createSimpleDriverDataSource(
					databaseConnectionProperties.getCreateDataSourceDriverName(),
					databaseConnectionProperties.getCreateDataSourceUsername(),
					databaseConnectionProperties.getCreateDataSourcePassword(),
					databaseConnectionProperties.getCreateDataSourceUrl());
		} else {
			return createSimpleDriverDataSource(
					databaseConnectionProperties.getDataSourceDriverName(),
					databaseConnectionProperties.getDataSourceUsername(),
					databaseConnectionProperties.getDataSourcePassword(),
					databaseConnectionProperties.getDataSourceUrl());
		}
	}

	private DataSource createSimpleDriverDataSource(final String driverName, final String username, final String password, final String url) {
		SimpleDataSource source = new SimpleDataSource();
		source.setDriverClassName(driverName);
		source.setUsername(username);
		source.setPassword(password);
		source.setUrl(url);
		try {
			return source.createDataSource();
		} catch (SQLException e) {
			LOG.error(String.format("An error has occured when creating the data source with driverName: %s, username: %s"
					+ ", and connection url: %s.", driverName, username, url), e);
			throw new SqlActionException(e.getMessage(), e);
		}
	}

	/**
	 * Extends {@link BasicDataSource} to expose createDataSource method.
	 */
	private class SimpleDataSource extends BasicDataSource {
		@Override
		public DataSource createDataSource() throws SQLException {
			return super.createDataSource();
		}
	}
}
