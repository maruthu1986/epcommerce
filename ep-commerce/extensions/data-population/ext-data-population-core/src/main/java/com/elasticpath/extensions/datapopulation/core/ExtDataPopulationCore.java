package com.elasticpath.extensions.datapopulation.core;

import java.util.Map;

import com.elasticpath.datapopulation.core.DataPopulationCore;
import com.elasticpath.datapopulation.core.action.DataPopulationAction;
import com.elasticpath.datapopulation.core.context.DataPopulationContext;

/**
 * The data population core contains all the action executors for each high level command (reset-db, update-db and filter-data).
 */
@SuppressWarnings("PMD.UnusedPrivateField")
public class ExtDataPopulationCore extends DataPopulationCore {

	private DataPopulationContext dataPopulationContext;
	private Map<String, DataPopulationAction> dataPopulationActions;

	/**
	 * Runs the high level command given as the commandKey.
	 * @param commandKey the command
	 */
    @Override
	public void runActionExecutor(final String commandKey) {
        super.runActionExecutor(commandKey);
	}
}
