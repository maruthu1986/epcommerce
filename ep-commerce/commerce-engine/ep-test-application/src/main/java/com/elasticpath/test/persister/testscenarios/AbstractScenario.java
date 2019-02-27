/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.persister.testscenarios;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.test.persister.TestDataPersisterFactory;

/**
 * Represents abstract scenario.
 */
public abstract class AbstractScenario {

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private TestDataPersisterFactory dataPersisterFactory;

	/**
	 * Initializes the scenario. Called automatically by TestApplicationContext.useScenario(...) If user wants to create scenario by his own (w/o
	 * use of TestApplicationContext) he needs to call the method explicitly.
	 */
	public abstract void initialize();

	protected TestDataPersisterFactory getDataPersisterFactory() {
		return dataPersisterFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
