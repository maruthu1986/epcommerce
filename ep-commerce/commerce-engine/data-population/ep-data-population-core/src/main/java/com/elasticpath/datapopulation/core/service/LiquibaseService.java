/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service;

import java.util.Map;
import javax.sql.DataSource;

import liquibase.exception.LiquibaseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.datapopulation.core.exceptions.LiquibaseActionException;
import com.elasticpath.datapopulation.core.service.liquibase.DpServiceSpringLiquibase;
import com.elasticpath.datapopulation.core.utils.DpResourceUtils;
import com.elasticpath.datapopulation.core.utils.DpUtils;

/**
 * Service to execute a Liquibase change log against the configured database.
 */
public class LiquibaseService {
	private static final Logger LOG = Logger.getLogger(LiquibaseService.class);

	private DpServiceSpringLiquibase standardSpringLiquibaseConfiguration;

	// Service methods

	/**
	 * Executes the given Liquibase change log.
	 *
	 * @param liquibaseChangelog           the path to the Liquibase change log to execute.
	 * @param liquibaseContexts            the contexts to execute the Liquibase change log with; may be null.
	 * @param dropFirst                    true if Liquibase should drop all the tables first, false otherwise.
	 * @param liquibaseChangelogParameters a map of parameters to pass to Liquibase to allow it to resolve any placeholders in the Liquibase file.
	 * @param dataSource                   the data source connections
	 * @throws LiquibaseActionException if Liquibase encountered an error executing the change log file.
	 */
	public void executeLiquibaseChangeLog(
			final String liquibaseChangelog,
			final String liquibaseContexts,
			final boolean dropFirst,
			final Map<String, String> liquibaseChangelogParameters,
			final DataSource dataSource) {

		if (StringUtils.isBlank(liquibaseChangelog)) {
			throw new LiquibaseActionException("Error: No Liquibase changelog file was specified.");
		}

		try {
			performLiquibaseUpdate(liquibaseChangelog, liquibaseContexts, dropFirst, liquibaseChangelogParameters, dataSource);
		} catch (final LiquibaseException e) {
			LOG.error("Error: Error attempting to execute Liquibase changelog file: '" + liquibaseChangelog
					+ "', see attached cause for details.", e);
			throw new LiquibaseActionException("Unable to execute Liquibase changelog file: '" + liquibaseChangelog
					+ "'. " + DpUtils.getNestedExceptionMessage(e), e);
		}
	}

	// Implementation methods
	private void performLiquibaseUpdate(
			final String liquibaseChangeLogGiven,
			final String liquibaseContexts,
			final boolean dropFirst,
			final Map<String, String> liquibaseChangelogParameters,
			final DataSource dataSource)
			throws LiquibaseException {
		if (dataSource == null) {
			throw new LiquibaseException("The data source connection is not set when performing a liquibase changelog");
		}
		final String liquibaseChangeLog = DpResourceUtils.getFileResourceUriByDefault(liquibaseChangeLogGiven, false);
		setStandardSpringLiquibaseConfigurationDataSource(dataSource);
		getStandardSpringLiquibaseConfiguration().update(liquibaseChangeLog, liquibaseContexts, dropFirst, liquibaseChangelogParameters);
	}

	// Getters and Setters

	/**
	 * Sets the standard liquibase configuration data source connection.
	 *
	 * @param dataSource the data population context
	 */
	private void setStandardSpringLiquibaseConfigurationDataSource(final DataSource dataSource) {
		this.standardSpringLiquibaseConfiguration.setDataSource(dataSource);
	}

	// Getters and Setters
	protected DpServiceSpringLiquibase getStandardSpringLiquibaseConfiguration() {
		return this.standardSpringLiquibaseConfiguration;
	}

	public void setStandardSpringLiquibaseConfiguration(final DpServiceSpringLiquibase standardSpringLiquibaseConfiguration) {
		this.standardSpringLiquibaseConfiguration = standardSpringLiquibaseConfiguration;
	}
}
