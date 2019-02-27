/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.log4j.Logger;

import com.elasticpath.datapopulation.core.exceptions.SqlActionException;
import com.elasticpath.datapopulation.core.utils.DpUtils;

/**
 * Service to execute SQL statements or a SQL file against the configured database.
 */
public class SqlService {
	/**
	 * The delimiter String used in SQL statements.
	 */
	protected static final String SQL_STATEMENT_DELIMITER = ";";
	private static final Logger LOG = Logger.getLogger(SqlService.class);

	/**
	 * Executes the given SQL statement(s), and/or file containing SQL statements to execute. If both are specified, both are executed, first the
	 * given SQL statement(s), followed by the given SQL file.
	 *
	 * @param sql              the SQL statement(s) to execute, delimited by the {@link #SQL_STATEMENT_DELIMITER}; may be null.
	 * @param sqlFile          the file containing SQL statements to execute.
	 * @param dataSource       the data source generated from the context settings
	 * @param isSendFullScript true if the script should be sent through fully
	 * @throws SqlActionException if there was a problem executing the SQL statements.
	 */
	public void executeSql(
			final String sql,
			final File sqlFile,
			final DataSource dataSource,
			final boolean isSendFullScript)
			throws SqlActionException {

		final boolean executeSqlStatement = executeSql(sql, dataSource);
		final boolean executeSqlFile = executeSqlFile(sqlFile, dataSource, isSendFullScript);

		if (!executeSqlStatement && !executeSqlFile) {
			throw new SqlActionException("Error: Neither a sql statement or sql file was specified, please specify one or the other");
		}
	}

	// Methods for executing a SQL statement.

	/**
	 * Executes the given SQL statement(s) using the {@link DataSource} provided}.
	 * If multiple SQL statements are to be executed these must be delimited using the {@link #SQL_STATEMENT_DELIMITER}.
	 *
	 * @param sql        the SQL to execute, may be null
	 * @param dataSource the data source generated from the context settings
	 * @return true if the SQL String is not null and contained at least one SQL statement; false otherwise.
	 * @throws SqlActionException if there was a problem executing the SQL statements.
	 */
	public boolean executeSql(final String sql, final DataSource dataSource) {
		final boolean result = StringUtils.isNotBlank(sql);

		if (result) {
			try {
				executeStatement(sql, dataSource);
			} catch (final SQLException e) {
				// Spring-Shell does not log the nested cause so log it ourselves and include the nested message in our exception thrown
				LOG.error("Error: Error attempting to execute SQL statement '" + sql + "', see attached cause for details.", e);
				throw new SqlActionException("Unable to execute SQL statement: '" + sql + "'. "
						+ DpUtils.getNestedExceptionMessage(e), e);
			}
		}

		return result;
	}

	/**
	 * Executes the given SQL statement String. It can in fact contain multiple SQL statements if delimited by a {@link #SQL_STATEMENT_DELIMITER}
	 * character.
	 *
	 * @param sql        the SQL to execute, must not be null.
	 * @param dataSource the data source generated from the context settings
	 * @throws SQLException if there was a problem connecting to the database.
	 */
	public void executeStatement(final String sql, final DataSource dataSource) throws SQLException {
		final ScriptRunner sqlRunner = createSqlRunner(dataSource);
		try {
			executeStatement(sqlRunner, sql);
		} finally {
			if (sqlRunner != null) {
				sqlRunner.closeConnection();
			}
		}
	}

	/**
	 * Uses the given {@link ScriptRunner} to execute the given SQL statement. It ensures that the statement is terminated with a
	 * {@link #SQL_STATEMENT_DELIMITER}, and if not adds one before calling {@link ScriptRunner}. Otherwise the call would fail.
	 *
	 * @param sqlRunner   the {@link ScriptRunner} to use.
	 * @param sqlPassedIn the SQL statement(s) to execute.
	 */
	protected void executeStatement(final ScriptRunner sqlRunner, final String sqlPassedIn) {
		// Ensure the SQL provided ends in a statement delimiter as the underlying ScriptRunner requires it, even for just one statement.
		String sql = sqlPassedIn;
		if (!sql.endsWith(SQL_STATEMENT_DELIMITER)) {
			sql += SQL_STATEMENT_DELIMITER;
		}

		sqlRunner.runScript(new StringReader(sql));
	}

	/**
	 * Executes the given file containing SQL statements. Each statement should be delimited with the {@link #SQL_STATEMENT_DELIMITER} even if on
	 * separate lines.
	 *
	 * @param sqlFile          the SQL file to execute, may be null.
	 * @param dataSource       the data source generated from the context settings
	 * @param isSendFullScript true if the script should be sent through fully
	 * @return true if the SQL file is not null; false otherwise.
	 * @throws SqlActionException if there was a problem executing the SQL file.
	 */
	public boolean executeSqlFile(final File sqlFile, final DataSource dataSource, final boolean isSendFullScript) {
		final boolean result = (sqlFile != null);

		if (result) {
			try {
				executeFile(sqlFile, dataSource, isSendFullScript);
			} catch (final SQLException | IOException e) {
				final String sqlFilePath = sqlFile.getAbsolutePath();
				LOG.error("Error: Error attempting to execute SQL file '" + sqlFilePath + "', see attached cause for details.", e);
				throw new SqlActionException("Unable to execute SQL file: '" + sqlFilePath + "'. "
						+ DpUtils.getNestedExceptionMessage(e), e);
			}
		}

		return result;
	}

	/**
	 * Executes the given file containing SQL statements. Each statement should be delimited with the {@link #SQL_STATEMENT_DELIMITER} even if on
	 * separate lines.
	 *
	 * @param sqlFile          the SQL file to execute, must not be null.
	 * @param dataSource       the data source generated from the context settings
	 * @param isSendFullScript true if the script should be sent through fully
	 * @throws SQLException if there was a problem connecting to the database.
	 * @throws IOException  if there was a problem reading the SQL file.
	 */
	public void executeFile(final File sqlFile, final DataSource dataSource, final boolean isSendFullScript) throws SQLException, IOException {
		final ScriptRunner sqlRunner = createSqlRunner(dataSource);
		try {
			executeFile(sqlRunner, sqlFile, isSendFullScript);
		} finally {
			if (sqlRunner != null) {
				sqlRunner.closeConnection();
			}
		}
	}


	/**
	 * Uses the given {@link ScriptRunner} to execute the given SQL file.
	 *
	 * @param sqlRunner        the {@link ScriptRunner} to use.
	 * @param sqlFile          the SQL file to execute.
	 * @param isSendFullScript true if the script should be sent through fully
	 * @throws IOException if there is a problem reading the SQL file.
	 */
	protected void executeFile(final ScriptRunner sqlRunner, final File sqlFile, final boolean isSendFullScript) throws IOException {
		LOG.info("Running SQL file: " + sqlFile.getAbsolutePath());

		if (!sqlFile.exists()) {
			throw new IllegalArgumentException("SQL File '"
					+ sqlFile.getAbsoluteFile() + "' does not exist, please check your arguments and try again.");
		}


		// Make sure we close the FileReader after processing
		try (final Reader fileReader = Files.newBufferedReader(sqlFile.toPath(), StandardCharsets.UTF_8)) {
			sqlRunner.setSendFullScript(isSendFullScript);
			sqlRunner.runScript(fileReader);
		}
	}

	// Factory methods

	/**
	 * Factory method to create a {@link ScriptRunner} instance using the appropriate database connection as provided by
	 * {@link javax.sql.DataSource}.
	 *
	 * @param dataSource the data source generated from the context settings
	 * @return a {@link ScriptRunner} instance using the appropriate database connection.
	 * @throws SQLException if there was a problem connecting to the database.
	 */
	protected ScriptRunner createSqlRunner(final DataSource dataSource) throws SQLException {
		return createSqlRunner(dataSource.getConnection());
	}

	/**
	 * Factory method to create a {@link ScriptRunner} with the given database {@link Connection}.
	 *
	 * @param connection the database {@link Connection} to use.
	 * @return a {@link ScriptRunner} with the given database {@link Connection}.
	 */
	protected ScriptRunner createSqlRunner(final Connection connection) {
		final ScriptRunner result = new ScriptRunner(connection);
		result.setStopOnError(true);
		return result;
	}
}
