/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.sql.DataSource;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;

/**
 * Implementation of StatusCheckerTarget which checks (pings) a {@link DataSource}.
 */
public class DatabaseStatusTargetImpl extends AbstractStatusCheckerTarget {

	private static final Logger LOG = Logger.getLogger(DatabaseStatusTargetImpl.class);

	private static final int NANOSECONDS_IN_1_MS = 1000000;

	private DataSource dataSource;

	private String testQuery;

	/**
	 * Checks {@link DataSource} connectivity.
	 * 
	 * @return the status
	 */
	@Override
	public Status check() {
		Status status;

		LOG.debug("Checking database status.");

		// Check JDBC connectivity.

		StopWatch watch = new StopWatch();
		watch.start();
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(testQuery)) {
				preparedStatement.execute();
				watch.stop();
				status = createStatus(StatusType.OK,
						"Database was successfully reached in " + watch.getNanoTime() / NANOSECONDS_IN_1_MS + "ms",
						null);
				LOG.debug("Successful database status.");
		} catch (Exception e) {
			LOG.error("Database connection failure. ", e);
			status = createStatus(StatusType.CRITICAL, "JDBC Connection failure", e.getMessage());
		}

		return status;
	}

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setTestQuery(final String testQuery) {
		this.testQuery = testQuery;
	}

}
