/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.email.dataimport;

import cucumber.api.java.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.elasticpath.cucumber.testexecutionlisteners.CucumberDatabaseTestExecutionListener;
import com.elasticpath.cucumber.testexecutionlisteners.CucumberJmsRegistrationTestExecutionListener;

/**
 * Responsible for setup and tear down of the UAT test context.
 */
@ContextConfiguration("/cucumber.xml")
@TestExecutionListeners(listeners = {
		CucumberJmsRegistrationTestExecutionListener.class,
		CucumberDatabaseTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class
})
public class CucumberDatabaseInitialiser {

	/**
	 * Hook for the initialisation of the UAT test context.  The heavy lifting is done by the TestExecutionListeners register on the class.
	 */
	@Before(order = 1)
	public void initialize() {
		// No initialization here but there may be in subclasses
	}


}
