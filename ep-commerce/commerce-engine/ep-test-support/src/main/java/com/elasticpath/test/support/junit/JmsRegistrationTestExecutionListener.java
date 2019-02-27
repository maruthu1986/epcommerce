/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.support.junit;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.elasticpath.test.support.jms.activemq.ActiveMqConnectionFactoryJndiBinderImpl;
import com.elasticpath.test.support.jndi.JndiContextManager;

/**
 * Registers an ActiveMQ connection factory with JNDI before each test class.
 */
public class JmsRegistrationTestExecutionListener extends AbstractTestExecutionListener {

	private final ActiveMqConnectionFactoryJndiBinderImpl activeMqConnectionFactoryJndiBinder;

	/**
	 * Constructor.
	 */
	public JmsRegistrationTestExecutionListener() {
		activeMqConnectionFactoryJndiBinder = new ActiveMqConnectionFactoryJndiBinderImpl(JndiContextManager.createJndiContextManager());
	}

	@Override
	public void beforeTestClass(final TestContext testContext) throws Exception {
		activeMqConnectionFactoryJndiBinder.bindNewActiveMqConnectionFactory();
	}

	@Override
	public void afterTestClass(final TestContext testContext) throws Exception {
		activeMqConnectionFactoryJndiBinder.unbindNewActiveMqConnectionFactory();
	}

}
