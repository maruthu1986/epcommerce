/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.persister.testscenarios;

import com.elasticpath.domain.store.Store;

/**
 * A Scenario which creates two default customers with associated customer session and empty shopping carts.
 * It has a dependency on SimpleStoreScenario, which must be run before this one.
 * This scenario is really designed to be used as part of a composite, although you can use it directly by
 * including it in a list of scenarios behind the SimpleStoreScenario.
 */
public class CustomerAndCustomerSessionsScenario extends AbstractScenario {
	private SimpleStoreScenario simpleStoreScenario;

	@Override
	public void initialize() {
		// Create customers with addresses and shippable/non shippable products.
		getDataPersisterFactory().getStoreTestPersister().persistDefaultCustomers(getStore());
	}

	public Store getStore() {
		return getSimpleStoreScenario().getStore();
	}

	protected SimpleStoreScenario getSimpleStoreScenario() {
		return simpleStoreScenario;
	}

	public void setSimpleStoreScenario(final SimpleStoreScenario simpleStoreScenario) {
		this.simpleStoreScenario = simpleStoreScenario;
	}
}
