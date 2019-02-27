/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.context.impl;

import com.elasticpath.datapopulation.core.context.DataPopulationContext;

/**
 * The context implementation for data population.
 */
public class DataPopulationContextImpl implements DataPopulationContext {

	private Object actionConfiguration;

	@Override
	public Object getActionConfiguration() {
		return actionConfiguration;
	}

	@Override
	public void setActionConfiguration(final Object actionConfiguration) {
		this.actionConfiguration = actionConfiguration;
	}
}
