/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.integration.junit;

import org.apache.log4j.Logger;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.support.jndi.JndiContextManager;

/**
 * JUnit framework hook to execute the {@link DirtiesDatabase} annotation.
 */
public class DatabaseHandlingTestExecutionListener extends AbstractTestExecutionListener {

	private static final Logger LOG = Logger.getLogger(DatabaseHandlingTestExecutionListener.class);

	private final JndiContextManager jndiContextManager;

	public DatabaseHandlingTestExecutionListener() {
		jndiContextManager = JndiContextManager.createJndiContextManager();
	}

	@Override
	public final int getOrder() {
		return 1;
	}

	@Override
	public void beforeTestClass(final TestContext testContext) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Performing database reset for test context [" + testContext + "].");
		}
		DatabaseTestExecutionListenerHelper.resetDatabase(testContext, jndiContextManager);
	}

	@Override
	public void beforeTestMethod(final TestContext testContext) throws Exception {
		final boolean classDirtiesDatabase = testContext.getTestClass().isAnnotationPresent(DirtiesDatabase.class);
		final boolean methodDirtiesDatabase = testContext.getTestMethod().isAnnotationPresent(DirtiesDatabase.class);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Performing database reset for test context [" + testContext + "].");
		}

		if (methodDirtiesDatabase || classDirtiesDatabase) {
			DatabaseTestExecutionListenerHelper.initializeSnapshot();
		}
	}

	@Override
	public void afterTestMethod(final TestContext testContext) throws Exception {
		final boolean classDirtiesDatabase = testContext.getTestClass().isAnnotationPresent(DirtiesDatabase.class);
		final boolean methodDirtiesDatabase = testContext.getTestMethod().isAnnotationPresent(DirtiesDatabase.class);

		if (LOG.isDebugEnabled()) {
			LOG.debug("After test method: context [" + testContext + "], class dirties database ["
					+ classDirtiesDatabase + "], method dirties database ["
					+ methodDirtiesDatabase + "].");
		}

		if (methodDirtiesDatabase || classDirtiesDatabase) {
			DatabaseTestExecutionListenerHelper.resetDatabase(testContext, jndiContextManager);
		}
	}
}