/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Unit test for {@link ApplicationLockManager}.
 */
public class ApplicationLockManagerTest {
	/**
	 * Time for the test to pause for, in order to give daemon thread enough time to wake up and do its job.
	 */
	private static final long TEST_SLEEPTIME = 400L;
	/**
	 * Idle timeout for sessions, in production, this should be 15-30 minutes, not 100ms.
	 */
	private static final long TEST_IDLE_TIMEOUT = 100L;
	/**
	 * Daemon thread sleep period, in production, this is around 5s.
	 */
	private static final long TEST_TIMER_PERIOD = 200L;


	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public TestContext context = new TestContext();

	private ApplicationLockManager applicationLockManager;
	private static final String ATTR_SESSION_DISPLAY =  "org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil#sessionDisplay";


	@Before
	public void setUp() {
		applicationLockManager = new TestApplicationLockManager();
		applicationLockManager.start();
	}
	@After public void tearDown() {
		applicationLockManager.getTimer().cancel();
	}

	@Test
	public void testSessionRemovedAfterTimeout() {
		//set low idle timeout of 1s
		((TestApplicationLockManager) applicationLockManager).setTestIdleTimeout(TEST_IDLE_TIMEOUT);

		Display display = new Display();
		UISession mockSession = context.getUISession();
		mockSession.setAttribute(ATTR_SESSION_DISPLAY, display);

		applicationLockManager.registerSession(mockSession);
		//verify session exists in lock manager
		Assert.assertTrue(applicationLockManager.getSessions().containsKey(mockSession.getId()));

		try {
			Thread.sleep(TEST_SLEEPTIME);
		} catch (InterruptedException exception) {
			Assert.fail(exception.getMessage());
			Thread.currentThread().interrupt();
		}

		//verify session no longer exists in list of tracked sessions.
		Assert.assertFalse(applicationLockManager.getSessions().containsKey(mockSession.getId()));

	}


	@Test
	public void testSessionTimeoutUpdatedAfterListenerEvent() {
		//set low idle timeout of 1s
		long longerTimeout = 200L;
		((TestApplicationLockManager) applicationLockManager).setTestIdleTimeout(longerTimeout);

		Display display = new Display();
		UISession mockSession = context.getUISession();
		mockSession.setAttribute(ATTR_SESSION_DISPLAY, display);

		applicationLockManager.registerSession(mockSession);


		//get the listener
		ApplicationLockManager.SessionHolder sessionHolder = applicationLockManager.getSessions().get(mockSession.getId());
		Listener listener = sessionHolder.getDisplayListeners().get(SWT.Activate);
		Long initialTimestamp = sessionHolder.getTimeStamp();

		//wait until the session is almost expired
		try {

			Thread.sleep(TEST_SLEEPTIME / 2);
		} catch (InterruptedException exception) {
			Assert.fail(exception.getMessage());
			Thread.currentThread().interrupt();
		}

		//simulate a UI action
		listener.handleEvent(new Event());

		//verify session timeout is updated.
		Assert.assertTrue(applicationLockManager.getSessions().containsKey(mockSession.getId()));
		Assert.assertTrue(applicationLockManager.getSessions().get(mockSession.getId()).getTimeStamp() > initialTimestamp);

	}
	/**
	 * Test Class to allow overriding of non-testable calls. And to override timout.
	 */
	private class TestApplicationLockManager extends ApplicationLockManager {

		private long testIdleTimeout;
		TestApplicationLockManager() {
			super(false);
		}

		@Override
		protected long getIdleTimeout() {
			return getTestIdleTimeout();
		}

		long getTestIdleTimeout() {
			return testIdleTimeout;
		}

		void setTestIdleTimeout(final long testIdleTimeout) {
			this.testIdleTimeout = testIdleTimeout;
		}

		@Override
		protected long getTimerPeriod() {
			return TEST_TIMER_PERIOD;
		}
	}
}
