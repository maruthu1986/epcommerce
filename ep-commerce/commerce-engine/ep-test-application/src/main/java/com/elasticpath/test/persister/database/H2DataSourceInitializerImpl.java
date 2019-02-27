/**
 * Copyright (c) Elastic Path Software Inc., 2008, 2011
 */
package com.elasticpath.test.persister.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.h2.store.fs.FileUtils;

import com.elasticpath.test.common.exception.DataSourceInitializerException;
import com.elasticpath.test.persister.TestConfig;

/**
 * H2 implementation of AbstractDataSourceInitializer.<br>
 * Runs with the H2 server mode.
 *
 * # database configuration settings for H2:<br>
 * db.rdbms=h2<br>
 * db.connection.driver_class=org.h2.Driver<br>
 *
 */
public class H2DataSourceInitializerImpl extends AbstractDataSourceInitializer {
	private static final Logger LOG = Logger
			.getLogger(H2DataSourceInitializerImpl.class);

	private RunMode runMode = RunMode.IN_MEMORY;
	private Properties properties;
	private String databaseLocation;

	private enum RunMode {IN_MEMORY, EMBEDDED, SERVER_MODE};

	public H2DataSourceInitializerImpl(final Properties properties) {
		super(properties);
		this.properties = properties;
		if ("h2mem".equals(properties.get(TestConfig.PROPERTY_RDBMS))) {
			runMode = RunMode.IN_MEMORY;
		} else if ("h2".equals(properties.get(TestConfig.PROPERTY_RDBMS))) {
			runMode = RunMode.SERVER_MODE;
		} else {
			runMode = RunMode.EMBEDDED;
		}
	}

	@Override
	public String getConnectionUrl() {
		if (runMode.equals(RunMode.IN_MEMORY)) {
			return "jdbc:h2:mem:" + getDatabaseName() + ";DB_CLOSE_DELAY=-1";
		} else if (runMode.equals(RunMode.SERVER_MODE)) {
			return "jdbc:h2:tcp://" + host + ":" + port + "/" + getDatabaseName();
		}
		return "jdbc:h2:file:" + getDatabaseLocation();
	}

	@Override
	public void dropAndCreateDatabase() {
		LOG.info("Using H2 database.");
		LOG.info("Erasing the entire database: " + getDatabaseName());

		Connection conn = null;
		Statement stmnt = null;
		try {
			conn = DriverManager.getConnection(
					getConnectionUrl(), username, password);
			stmnt = conn.createStatement();

			stmnt.execute("DROP ALL OBJECTS");

		} catch (SQLException exception) {
			LOG.error("Error dropping objects", exception);
			// no need to do anything
		} finally {
			close(stmnt);
			close(conn);
		}

		try {
			// H2 autocreates database upon first connection to them
			conn = DriverManager.getConnection(
					getConnectionUrl(), username, password);
		} catch (Exception exception) {
			LOG.fatal("Failed to create test database", exception);
			throw new DataSourceInitializerException(
					"Failed to create test database", exception);
		} finally {
			close(conn);
		}

	}

	private String getDatabaseLocation() {
		if (databaseLocation == null) {
			String newLocation;
			if (!new File(getDatabaseName()).isAbsolute()) {
				newLocation = FilenameUtils.concat(properties.getProperty(TestConfig.PROPERTY_RUNNING_DIRECTORY), getDatabaseName());
			} else {
				newLocation = getDatabaseName();
			}
			databaseLocation = newLocation;
		}
		return databaseLocation;
	}

	@Override
	public void initializeSnapshot() {
		final String testClassName = getSnapshotFileName();
		try {
			if (!FileUtils.exists("memFS:" + testClassName)) {
				Connection conn = DriverManager.getConnection(getConnectionUrl(), getUsername(), getPassword());
				Statement stat = conn.createStatement();
				stat.execute("SCRIPT TO 'memFS:" + testClassName + "'");
				stat.close();
				conn.close();
				LOG.debug("Initialized snapshot - created file: " + testClassName);
			}
		} catch (SQLException exception) {
			LOG.fatal("Failed to initialize snapshot ", exception);
			throw new DataSourceInitializerException(
					"Failed to create test database", exception);
		}
	}

	@Override
	public String resetDatabase() throws SQLException {
		String connectionURL = getConnectionUrl();
		final String testClassName = getSnapshotFileName();
		if (FileUtils.exists("memFS:" + testClassName)) {

			Connection conn = DriverManager.getConnection(getConnectionUrl(), getUsername(), getPassword());
			Statement stat = conn.createStatement();

			stat.execute("DROP ALL OBJECTS");
			stat.close();
			conn.close();
			connectionURL = getConnectionUrl() + ";INIT=runscript from 'memFS:" + testClassName + "'";

			conn = DriverManager.getConnection(connectionURL, getUsername(), getPassword());
			conn.close();

			LOG.debug("Reset H2 database to existing snapshot");
		} else {
			LOG.debug("Drop and create H2 database to fresh instance");
			dropAndCreateDatabase();
		}
		return connectionURL;
	};

}