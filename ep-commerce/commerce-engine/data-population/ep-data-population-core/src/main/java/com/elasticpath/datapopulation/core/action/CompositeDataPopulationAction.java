/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.action;

import java.util.List;

import com.elasticpath.datapopulation.core.context.DataPopulationContext;

/**
 * The executor which contains the list of actions to execute. Follows the composite pattern.
 */
public class CompositeDataPopulationAction implements DataPopulationAction {

	private List<DataPopulationAction> dataPopulationActions;

	/**
	 * Executes the list of actions to facilitate a high-level data population process.
	 *
	 * @param dataPopulationContext the data population context
	 */
	public void execute(final DataPopulationContext dataPopulationContext) {
		for (DataPopulationAction action : getDataPopulationActions()) {
			action.execute(dataPopulationContext);
		}
	}

	//Getters and Setters
	public List<DataPopulationAction> getDataPopulationActions() {
		return dataPopulationActions;
	}

	public void setDataPopulationActions(final List<DataPopulationAction> dataPopulationActions) {
		this.dataPopulationActions = dataPopulationActions;
	}
}
