/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber;

import cucumber.api.java.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.elasticpath.cucumber.testexecutionlisteners.CucumberDatabaseTestExecutionListener;
import com.elasticpath.cucumber.testexecutionlisteners.CucumberJmsRegistrationTestExecutionListener;

/**
 * Test database initialization.
 */

@ContextConfiguration("/cucumber.xml")
@TestExecutionListeners(listeners = {
		CucumberDatabaseTestExecutionListener.class,
		CucumberJmsRegistrationTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class
})
public class CucumberDatabaseInitializer {


	/**
	 * Uses Before annotation with a lower number of order to execute database initialization for every scenario before
	 * other Before methods hooked for tags.
	 */
	@Before(order = 1)
	public void initializeDatabase() {
		// do nothing
	}
}
