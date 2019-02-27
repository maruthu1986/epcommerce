/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerTagStrategy;

/**
 * Keeps track of CustomerTagStrategy services.
 */
@Singleton
@Named("customerTagStrategyRegistry")
class CustomerTagStrategyRegistry {

	private final Collection<CustomerTagStrategy> tagStrategies = new CopyOnWriteArrayList<>();


	/**
	 * Called by blueprint to load a strategy instance.
	 *
	 * @param strategy the strategy to load
	 */
	public void loadStrategy(final CustomerTagStrategy strategy) {
		if (strategy != null) {
			tagStrategies.add(strategy);
		}
	}

	/**
	 * Called by blueprint to unload a strategy instance.
	 *
	 * @param strategy the strategy to unload
	 */
	public void unloadStrategy(final CustomerTagStrategy strategy) {
		if (strategy != null) {
			tagStrategies.remove(strategy);
		}
	}

	Collection<CustomerTagStrategy> getStrategies() {
		return tagStrategies;
	}
}
