/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber;

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
public class CucumberInitialiser {


}
