/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.test.support;

import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.TransactedDefinition;
import org.apache.camel.spring.spi.SpringTransactionPolicy;

import com.elasticpath.test.support.transaction.spring.DummyPlatformTransactionManager;

/**
 * Utility class used to manage the registry used in Apache Camel unit tests.
 */
public class TransactionPolicyRegistryManager {

	private final JndiRegistry registry;

	/**
	 * Constructor.
	 * 
	 * @param registry the Camel registry
	 */
	public TransactionPolicyRegistryManager(final JndiRegistry registry) {
		this.registry = registry;
	}

	/**
	 * In normal operation, RouteBuilders retrieve a Spring-managed bean representing the default 'PROPAGATION_REQUIRED' transaction policy from the
	 * Spring application context. For the purpose of unit testing we must simulate this.
	 */
	public void registerDefaultTransactionPolicy() {
		registry.bind(TransactedDefinition.PROPAGATION_REQUIRED, new SpringTransactionPolicy(new DummyPlatformTransactionManager()));
	}

}
