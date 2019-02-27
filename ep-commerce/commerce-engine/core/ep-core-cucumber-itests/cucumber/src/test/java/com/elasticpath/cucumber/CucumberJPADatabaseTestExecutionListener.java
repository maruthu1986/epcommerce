/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cucumber;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.elasticpath.test.support.jndi.JndiContextManager;

/**
 * Cucumber specific test execution listener to manage database used in test context.
 */
public class CucumberJPADatabaseTestExecutionListener extends AbstractTestExecutionListener {

	private static final String JNDI_NAME = "java:comp/env/jdbc/epjndi";

	private static final Logger LOG = Logger.getLogger(CucumberJPADatabaseTestExecutionListener.class);

	@Override
	public final int getOrder() {
		return 2;
	}

	private final JndiContextManager jndiContextManager;

	/**
	 * test.
	 */
	public CucumberJPADatabaseTestExecutionListener() {
		jndiContextManager = JndiContextManager.createJndiContextManager();
		ApplicationContext ctx = new ClassPathXmlApplicationContext("datasource.xml");
		final DataSource dataSource = (DataSource) ctx.getBean("dataSource");


		jndiContextManager.unbind(JNDI_NAME);
		jndiContextManager.bind(JNDI_NAME, dataSource);


		//DatabaseTestExecutionListenerHelper.resetDatabase(null, jndiContextManager);
	}

	@Override
	public void beforeTestClass(final TestContext testContext) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("performing database reset for test context [" + testContext + "].");
		}

		ApplicationContext ctx = new ClassPathXmlApplicationContext("datasource.xml");
		final DataSource dataSource = (DataSource) ctx.getBean("dataSource");


		jndiContextManager.unbind(JNDI_NAME);
		jndiContextManager.bind(JNDI_NAME, dataSource);
	}

	@Override
	public void prepareTestInstance(final TestContext testContext) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("performing database reset for test context [" + testContext + "].");
		}

		ApplicationContext ctx = new ClassPathXmlApplicationContext("datasource.xml");
		final DataSource dataSource = (DataSource) ctx.getBean("dataSource");


		jndiContextManager.unbind(JNDI_NAME);
		jndiContextManager.bind(JNDI_NAME, dataSource);
	}

}
