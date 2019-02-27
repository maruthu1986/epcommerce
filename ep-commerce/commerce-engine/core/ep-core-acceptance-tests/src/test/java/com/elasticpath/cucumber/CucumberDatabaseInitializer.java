/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cucumber;

import cucumber.api.java.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.elasticpath.cucumber.testexecutionlisteners.CucumberDatabaseTestExecutionListener;
import com.elasticpath.cucumber.testexecutionlisteners.CucumberJmsRegistrationTestExecutionListener;

/**
 *  Test database initialization.
 */
@TestExecutionListeners(listeners = {
	CucumberJmsRegistrationTestExecutionListener.class,
	CucumberDatabaseTestExecutionListener.class,
	DependencyInjectionTestExecutionListener.class
})
@ContextConfiguration("/cucumber.xml")
public class CucumberDatabaseInitializer {
	
	/**
	 * Uses Before annotation with a lower number of order to execute database initialization for every scenario before
	 * other Before methods hooked for tags.
	 */
	@Before(order = CucumberConstants.CUCUMBER_HOOK_METHOD_ORDERING_1)
	public void initializeDatabase() {
		// do nothing
	}
}
