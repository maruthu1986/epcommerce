/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.support.jms.activemq;

import javax.jms.ConnectionFactory;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.test.support.jndi.JndiContextManager;

/**
 * Test class for {@link ActiveMqConnectionFactoryJndiBinderImpl}.
 */
public class ActiveMqConnectionFactoryJndiBinderImplTest {

	private static final String JNDI_NAME = ActiveMqConnectionFactoryJndiBinderImpl.JNDI_NAME;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	/**
	 * Verifies an and that an ActiveMQ Connection Factory is bound with the JndiContextManager.
	 */
	@Test
	public void verifyBindDelegatesToJndiContextManager() throws Exception {
		final JndiContextManager spyJndiContextManager = context.mock(JndiContextManager.class);
		final ConnectionFactory dummyConnectionFactory = context.mock(ConnectionFactory.class);

		final ActiveMqConnectionFactoryJndiBinderImpl binder = new ActiveMqConnectionFactoryJndiBinderImpl(spyJndiContextManager) {
			@Override
			ConnectionFactory createConnectionFactory() {
				return dummyConnectionFactory;
			}
		};

		context.checking(new Expectations() {
			{
				oneOf(spyJndiContextManager).bind(JNDI_NAME, dummyConnectionFactory);
			}
		});

		binder.bindNewActiveMqConnectionFactory();
	}

	/**
	 * Verifies the connection factory's JNDI name is unbound with the  JndiContextManager.
	 */
	@Test
	public void verifyUnbindDelegatesToJndiContextManager() throws Exception {
		final JndiContextManager spyJndiContextManager = context.mock(JndiContextManager.class);

		final ActiveMqConnectionFactoryJndiBinderImpl binder = new ActiveMqConnectionFactoryJndiBinderImpl(spyJndiContextManager);

		context.checking(new Expectations() {
			{
				oneOf(spyJndiContextManager).unbind(JNDI_NAME);
			}
		});

		binder.unbindNewActiveMqConnectionFactory();
	}

}