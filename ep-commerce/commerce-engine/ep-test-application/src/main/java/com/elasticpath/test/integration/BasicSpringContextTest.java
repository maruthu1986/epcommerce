/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.test.integration.junit.DatabaseHandlingTestExecutionListener;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.support.junit.JmsRegistrationTestExecutionListener;

/**
 * Basic Spring Context Test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-context.xml")
@SuppressWarnings("PMD.AbstractNaming")
@TestExecutionListeners({
		JmsRegistrationTestExecutionListener.class,
		DatabaseHandlingTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class
})
@DirtiesContext
public abstract class BasicSpringContextTest {

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private TestApplicationContext tac;
	
	/**
	 * @return the tac
	 */
	protected TestApplicationContext getTac() {
		return tac;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
