/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.action.impl;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.datapopulation.core.action.DataPopulationAction;
import com.elasticpath.datapopulation.core.context.DataPopulationContext;
import com.elasticpath.datapopulation.core.context.configurer.LiquibaseActionConfiguration;
import com.elasticpath.datapopulation.core.exceptions.DatabaseUpdateActionException;

/**
 * This action sets up the schema resources needed by {@link RunLiquibaseActionImpl}.
 */
public class InitializeSchemaActionImpl implements DataPopulationAction {

	private static final Logger LOG = Logger.getLogger(InitializeSchemaActionImpl.class);

	@Autowired
	@Qualifier("schemaInitChangelog")
	private File schemaInitChangeLogFile;

	@Autowired
	private RunLiquibaseActionImpl invokeLiquibaseAction;

	@Override
	public void execute(final DataPopulationContext context) {
		// Check that change log file now exists
		if (!schemaInitChangeLogFile.exists()) {
			throw new DatabaseUpdateActionException("Error: Unable to find filtered Liquibase change log file : "
					+ schemaInitChangeLogFile.getAbsolutePath());
		}

		LiquibaseActionConfiguration actionConfiguration = new LiquibaseActionConfiguration();
		actionConfiguration.setLiquibaseChangelog(schemaInitChangeLogFile.getAbsolutePath());
		actionConfiguration.setLiquibaseContexts(null);
		actionConfiguration.setDropFirst(false);
		actionConfiguration.setUserAdminConnections(false);
		actionConfiguration.setLiquibaseChangelogParameters(null);
		context.setActionConfiguration(actionConfiguration);

		LOG.info("Initializing the Schema using Liquibase change log file '" + actionConfiguration.getLiquibaseChangelog());
		invokeLiquibaseAction.execute(context);
	}
}
