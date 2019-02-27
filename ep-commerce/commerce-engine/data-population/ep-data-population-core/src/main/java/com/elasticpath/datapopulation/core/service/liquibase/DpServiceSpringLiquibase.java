/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.liquibase;

import java.util.Map;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

/**
 * DpCliSpringLiquibase is an extension of Liquibase's {@link SpringLiquibase} which allows a Liquibase changelog file to be run from within a
 * Spring context. This extension provides additonal API methods to allow it to be run on-demand rather than at Spring context startup.
 */
public class DpServiceSpringLiquibase extends SpringLiquibase {
	/**
	 * Invokes Liquibase for the given parameters and then calls {@link #afterPropertiesSet()} to perform the Liquibase update.
	 * Non-threadsafe since the super-class uses instance variables to pass the state around rather than passing the state directly on method calls.
	 *
	 * @param liquibaseChangeLog           the Liquibase changelog file to execute.
	 * @param liquibaseContexts            the Liquibase contexts to use when executing the changelog.
	 * @param dropFirst                    whether Liquibase should first drop all objects owned by the configured database user.
	 * @param liquibaseChangelogParameters any parameters that should be passed through for use by the Liquibase changelog, may be null.
	 * @throws LiquibaseException if any exception occurs processing this request.
	 */
	public void update(final String liquibaseChangeLog, final String liquibaseContexts, final boolean dropFirst,
					   final Map<String, String> liquibaseChangelogParameters) throws LiquibaseException {
		// First setup the state
		setChangeLog(liquibaseChangeLog);
		setContexts(liquibaseContexts);
		setDropFirst(dropFirst);
		setChangeLogParameters(liquibaseChangelogParameters);

		// Next switch the flag on so that the Liquibase update runs
		setShouldRun(true);

		// Finally we can call the entry point normally called by Spring; this performs the update.
		try {
			afterPropertiesSet();
		} finally {
			// Probably not required but after the run, turn off the shouldRun property, so it is only active through the duration of this method.
			setShouldRun(false);
		}
	}

}
