/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.datapopulation.core.action.DataPopulationAction;
import com.elasticpath.datapopulation.core.context.DataPopulationContext;
import com.elasticpath.datapopulation.core.context.DatabaseConnectionProperties;
import com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException;

/**
 * The data population core contains all the action executors for each high level command (reset-db, update-db and filter-data).
 */
public class DataPopulationCore {

	private DataPopulationContext dataPopulationContext;
	private Map<String, DataPopulationAction> dataPopulationActions;

	@Autowired
	@Qualifier("databaseConnectionProperties")
	private DatabaseConnectionProperties databaseConnectionProperties;

	/**
	 * Runs the high level command given as the commandKey.
	 *
	 * @param commandKey the command
	 */
	public void runActionExecutor(final String commandKey) {
		DataPopulationAction dpActionExecutor = dataPopulationActions.get(commandKey);
		if (dpActionExecutor == null) {
			throw new DataPopulationActionException("The command " + commandKey
					+ " is not a known command in data population");
		}

		dpActionExecutor.execute(dataPopulationContext);
	}

	// Getters and Setters
	public void setDataPopulationActions(final Map<String, DataPopulationAction> dataPopulationActions) {
		this.dataPopulationActions = dataPopulationActions;
	}

	public DatabaseConnectionProperties getDatabaseConnectionProperties() {
		return databaseConnectionProperties;
	}

	public DataPopulationContext getDataPopulationContext() {
		return dataPopulationContext;
	}

	public void setDataPopulationContext(final DataPopulationContext dataPopulationContext) {
		this.dataPopulationContext = dataPopulationContext;
	}

	/**
	 * Sets the resource wrapper when the resources cannot be determined by the context.
	 *
	 * @param actionConfiguration the resource the resource configuring the action
	 */
	public void setActionConfiguration(final Object actionConfiguration) {
		dataPopulationContext.setActionConfiguration(actionConfiguration);
	}
}
