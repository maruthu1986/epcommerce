/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.action.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.datapopulation.core.action.AbstractDataSourceAccessAction;
import com.elasticpath.datapopulation.core.context.DataPopulationContext;
import com.elasticpath.datapopulation.core.context.configurer.LiquibaseActionConfiguration;
import com.elasticpath.datapopulation.core.exceptions.LiquibaseActionException;
import com.elasticpath.datapopulation.core.service.LiquibaseService;

/**
 * This action calls LiquibaseService and executes the change log provided by the resource wrapper.
 */
public class RunLiquibaseActionImpl extends AbstractDataSourceAccessAction {

	@Autowired
	private LiquibaseService liquibaseService;

	@Override
	public void execute(final DataPopulationContext context) throws LiquibaseActionException {
		Object wrapper = context.getActionConfiguration();
		if (!(wrapper instanceof LiquibaseActionConfiguration)) {
			throw new LiquibaseActionException("Error: No Liquibase parameters provided within a resource wrapper.");
		}

		LiquibaseActionConfiguration liquibaseActionConfiguration = (LiquibaseActionConfiguration) wrapper;
		final String liquibaseChangelog = liquibaseActionConfiguration.getLiquibaseChangelog();

		if (StringUtils.isBlank(liquibaseChangelog)) {
			throw new LiquibaseActionException("Error: No Liquibase changelog file was configured for this command. ");
		}

		liquibaseService.executeLiquibaseChangeLog(
				liquibaseChangelog,
				liquibaseActionConfiguration.getLiquibaseContexts(),
				liquibaseActionConfiguration.isDropFirst(),
				liquibaseActionConfiguration.getLiquibaseChangelogParameters(),
				createDataSource(liquibaseActionConfiguration.isUseAdminConnections()));
	}
}
