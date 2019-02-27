/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cucumber;

import cucumber.api.java.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.elasticpath.cucumber.testexecutionlisteners.CucumberJmsRegistrationTestExecutionListener;

/**
 * Test database initialization.
 */
@ContextConfiguration("/cucumber.xml")
@TestExecutionListeners(listeners = {
		CucumberJPADatabaseTestExecutionListener.class,
		CucumberJmsRegistrationTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class
})
public class CucumberDatabaseInitializer {
	private static final int CUCUMBER_HOOK_METHOD_ORDERING_1 = 1;

	/**
	 * Uses Before annotation with a lower number of order to execute database initialization for every scenario before
	 * other Before methods hooked for tags.
	 */
	@Before(order = CUCUMBER_HOOK_METHOD_ORDERING_1)
	public void initializeDatabase() {
		//nothing
	}
}
