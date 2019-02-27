/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.order.jobs.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.misc.impl.DatabaseServerTimeServiceImpl;
import com.elasticpath.settings.test.support.SimpleSettingValueProvider;

/**
 * Unit tests for {@link OrderLockCleanupJobImpl}.
 */
public class OrderLockCleanupJobImplTest  {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private OrderLockCleanupJobImpl orderLockCleanupJob;
	
	private TimeService timeService;

	/**
	 * Prepare for the testing of the order lock cleanup job.
	 */
	@Before
	public void setUp() {
		timeService = new DatabaseServerTimeServiceImpl() {

			/**
			 * @return current time
			 */
			@Override
			public Date getCurrentTime() {
				return new Date();
			}
			
		};

		orderLockCleanupJob = new OrderLockCleanupJobImpl();
		orderLockCleanupJob.setTimeService(timeService);
	}

	/**
	 * Tests that a null pointer exception is thrown when the minutes until cleanup setting value contains no value.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetOrderLockCleanupSettingNullException() {
		orderLockCleanupJob.setBatchSizeProvider(new SimpleSettingValueProvider<>(1));
		orderLockCleanupJob.setStaleLockThresholdMinsProvider(new SimpleSettingValueProvider<>((Integer) null));
		orderLockCleanupJob.cleanUpOrderLocks();
	}

	/**
	 * Tests that a null pointer exception is thrown when the batch setting value contains no value.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetOrderLockBatchSizeSettingNullException() {
		orderLockCleanupJob.setBatchSizeProvider(new SimpleSettingValueProvider<>((Integer) null));
		orderLockCleanupJob.setStaleLockThresholdMinsProvider(new SimpleSettingValueProvider<>(1));
		orderLockCleanupJob.cleanUpOrderLocks();
	}

	/**
	 * Ensures that the isBatchSizeValid returns true if the value that is
	 * provided is of a positive value.
	 */
	@Test
	public void testIsBatchSizeValid() {
		final int batchSize = 5;
		assertTrue("Batch size should be valid for input " + batchSize, orderLockCleanupJob.isBatchSizeValid(batchSize));
	}

	/**
	 * Ensures that the isBatchSizeValid returns false if the value that is 
	 * provided is of a negative value.
	 */
	@Test
	public void testIsBatchSizeValidNegative() {
		final int batchSize = -10;
		assertFalse("Batch size should be invalid for input " + batchSize, orderLockCleanupJob.isBatchSizeValid(batchSize));
	}

	/**
	 * Ensures that the isMinsToExpireLocksValid return true if the value that is
	 * provided is of a positive value.
	 */
	@Test
	public void testMinsToExpireLocksValid() {
		final int minsToExpire = 5;
		assertTrue("Mins to expire should be valid for input " + minsToExpire, orderLockCleanupJob.isMinsToExpireLocksValid(minsToExpire));
	}

	/**
	 * Ensures that the isMinsToExpireLocksValid returns false if the value that is
	 * provided is of a negative value.
	 */
	@Test
	public void testMinsToExpireLocksValidNegative() {
		final int minsToExpire = -10;
		assertFalse("Mins to expire should be invalid for input " + minsToExpire, orderLockCleanupJob.isMinsToExpireLocksValid(minsToExpire));
	}

}
