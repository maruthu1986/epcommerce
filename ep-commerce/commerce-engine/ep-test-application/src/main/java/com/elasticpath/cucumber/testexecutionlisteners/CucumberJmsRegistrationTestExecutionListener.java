/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cucumber.testexecutionlisteners;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.elasticpath.test.support.jms.activemq.ActiveMqConnectionFactoryJndiBinderImpl;
import com.elasticpath.test.support.jndi.JndiContextManager;

/**
 * Cucumber specific test execution listener to manage the JMS broker used in the test context.
 */
public class CucumberJmsRegistrationTestExecutionListener extends AbstractTestExecutionListener {

	private final ActiveMqConnectionFactoryJndiBinderImpl activeMqConnectionFactoryJndiBinder;

	public CucumberJmsRegistrationTestExecutionListener() {
		activeMqConnectionFactoryJndiBinder = new ActiveMqConnectionFactoryJndiBinderImpl(JndiContextManager.createJndiContextManager());
		activeMqConnectionFactoryJndiBinder.bindNewActiveMqConnectionFactory();
	}

	@Override
	public void prepareTestInstance(final TestContext testContext) throws Exception {
		activeMqConnectionFactoryJndiBinder.bindNewActiveMqConnectionFactory();
	}

}