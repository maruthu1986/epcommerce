/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.support.jms.activemq;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.elasticpath.test.support.jndi.JndiContextManager;

/**
 * Binds an ActiveMQConnectionFactory to the default JNDI name for the EP JMS ConnectionFactory.
 */
public class ActiveMqConnectionFactoryJndiBinderImpl {

	/** The JNDI name at which to bind the connection factory. */
	static final String JNDI_NAME = "java:comp/env/jms/JMSConnectionFactory";

	/** The URL at which the JMS broker is located. */
	static final String BROKER_URL = "vm://localhost?broker.persistent=false";

	private final JndiContextManager jndiContextManager;

	/**
	 * Constructor.
	 * 
	 * @param jndiContextManager the JNDI context manager
	 */
	public ActiveMqConnectionFactoryJndiBinderImpl(final JndiContextManager jndiContextManager) {
		this.jndiContextManager = jndiContextManager;
	}

	/**
	 * Binds the ActiveMQConnectionFactory into JNDI.
	 */
	public void bindNewActiveMqConnectionFactory() {
		jndiContextManager.bind(JNDI_NAME, createConnectionFactory());
	}

	/**
	 * Unbinds the ActiveMQConnectionFactory from the JNDI context.
	 */
	public void unbindNewActiveMqConnectionFactory() {
		jndiContextManager.unbind(JNDI_NAME);
	}

	/**
	 * Returns a new {@link ActiveMQConnectionFactory}.
	 * 
	 * @return a new {@link ActiveMQConnectionFactory}
	 */
	ConnectionFactory createConnectionFactory() {
		return new ActiveMQConnectionFactory(BROKER_URL);
	}

}