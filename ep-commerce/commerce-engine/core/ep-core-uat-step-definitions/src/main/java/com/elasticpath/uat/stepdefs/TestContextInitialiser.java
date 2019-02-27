/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.stepdefs;

import java.util.List;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.elasticpath.cucumber.testexecutionlisteners.CucumberDatabaseTestExecutionListener;
import com.elasticpath.cucumber.testexecutionlisteners.CucumberJmsRegistrationTestExecutionListener;

/**
 * Responsible for setup and tear down of the UAT test context.
 */
@TestExecutionListeners(listeners = {
		CucumberJmsRegistrationTestExecutionListener.class,
		CucumberDatabaseTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class
})
public class TestContextInitialiser {

	@Autowired
	private List<CamelContext> contexts;

	/**
	 * Hook for the initialisation of the UAT test context.  The heavy lifting is done by the TestExecutionListeners register on the class.
	 */
	@Before(order = 100)
	public void initialize() {
		// No initialization here but there may be in subclasses
	}

	@After
	public void stopCamelContexts() throws Exception {
		for (final CamelContext context : contexts) {
			context.stop();
		}
	}

}
