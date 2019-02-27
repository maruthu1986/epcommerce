/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.context;

/**
 * The context of the data population that will be pass down the pipeline during execution of a data population action.
 */
public interface DataPopulationContext {

	/**
	 * Gets the resources wrapper that contains the configuration needed to run
	 * an action at a later point down the pipeline.
	 *
	 * @return actionConfiguration the resource configuring the action
	 */
	Object getActionConfiguration();

	/**
	 * Sets the resource needed to run a specific action at
	 * a later point down the pipeline that cannot be wired via Spring.
	 * <p>
	 * Calling this will replace the old configuration.
	 *
	 * @param actionConfiguration the resource configuring the action
	 */
	void setActionConfiguration(Object actionConfiguration);
}
