/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber;

import javax.inject.Inject;
import javax.inject.Named;

import cucumber.api.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.store.Store;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 *  Simple Store Scenario initialization.
 */
public class SimpleStoreScenarioInitializer {

	@Inject
	@Named("simpleStoreScenarioHolder")
	private ScenarioContextValueHolder<SimpleStoreScenario> simpleStoreScenarioHolder;
	
	@Inject
	@Named("storeHolder")
	private ScenarioContextValueHolder<Store> storeHolder;
	
	@Autowired
	private TestApplicationContext tac;
	
	/**
	 * Uses Before annotation with a lower number of order to execute simple store scenario initialization for every 
	 * Cucumber feature/scenario before other Before methods hooked for tags.
	 */
	@Before(order = CucumberConstants.CUCUMBER_HOOK_METHOD_ORDERING_100)
	public void initializeSimpleStoreScenario() {
		
		tac.useScenario(SimpleStoreScenario.class);
		
		SimpleStoreScenario scenario = (SimpleStoreScenario) tac.getScenario(SimpleStoreScenario.class);
		
		simpleStoreScenarioHolder.set(scenario);
		storeHolder.set(scenario.getStore());
	}
}
