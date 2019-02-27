/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.order.jobs.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.domain.order.jobs.OrderLockCleanupJob;
import com.elasticpath.domain.order.jobs.OrderLockCleanupResult;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderLockService;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Job which will remove order locks that have expired past a defined amount of time, which
 * is determined by a setting.
 */
public class OrderLockCleanupJobImpl implements OrderLockCleanupJob {

	private OrderLockService orderLockService;
	
	private TimeService timeService;
	
	private static final String BATCH_JOB_NAME = "CleanupOrderLocksJob";
	
	private static final Logger LOGGER = Logger.getLogger(OrderLockCleanupJobImpl.class);

	private SettingValueProvider<Integer> batchSizeProvider;

	private SettingValueProvider<Integer> staleLockThresholdMinsProvider;

	@Override
	public OrderLockCleanupResult cleanUpOrderLocks() {
		final long startTime = System.currentTimeMillis();
		LOGGER.debug("Start cleanup order locks quartz job at: " + new Date(startTime));
		
		final int batchSize = getBatchSize();
		final int minsToExpireLocks = getStaleLockThresholdMins();

		if (isBatchSizeValid(batchSize) && isMinsToExpireLocksValid(minsToExpireLocks)) {
			LOGGER.debug("Cleanup order locks quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
			return executeCleanup(batchSize, minsToExpireLocks);
		}
		
		LOGGER.debug("Cleanup order locks quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
		return new OrderLockCleanupResultImpl(0, 0, 0);
	}

	private OrderLockCleanupResultImpl executeCleanup(final int batchSize, final int minsToExpireLocks) {
		long startTime = System.currentTimeMillis();
		
		List<OrderLock> orderLocks = null;
		long time = getOrderLockDeleteTime(minsToExpireLocks).getTime();
		int totalNumberLocksRemoved = 0;
		int totalNumberBatchRuns = 0;
		
		while (orderLocks == null || orderLocks.size() == batchSize) {
			totalNumberBatchRuns++;
			orderLocks = getOrderLockService().findAllOrderLocksBeforeDate(time, 0, batchSize);
			totalNumberLocksRemoved += releaseOrderLocks(orderLocks);
		}
		
		long endTime = System.currentTimeMillis();
		return new OrderLockCleanupResultImpl(totalNumberBatchRuns, totalNumberLocksRemoved,  endTime - startTime);
	}

	private int releaseOrderLocks(final List<OrderLock> orderLocks) {
		int numberOfLocksRemoved = 0;
		for (OrderLock orderLock : orderLocks) {
			try {
				getOrderLockService().forceReleaseOrderLock(orderLock);
				numberOfLocksRemoved++;
				
			} catch (EpServiceException e) {
				LOGGER.error(BATCH_JOB_NAME + " Failed to release order lock " + orderLock.getOrder().getOrderNumber(), e);
			}
		}
		return numberOfLocksRemoved;
	}

	/**
	 * Checks if the value provided in the "minsBeforeCleanUp" setting is valid, being greater
	 * then or equal to zero.
	 *
	 * @param minsToExpireLocks - the value to check if valid
	 * @return true if valid, false otherwise
	 */
	boolean isMinsToExpireLocksValid(final int minsToExpireLocks) {
		
		if (minsToExpireLocks < 0) {
			LOGGER.error(BATCH_JOB_NAME + " Failed: minsToExpireLocks must be >= 0");
			return false;
		}
		
		return true;
	}

	/**
	 * Checks if the value provided in the "batchSize" setting is valid, being greater then or
	 * equal to zero.
	 *
	 * @param batchSize - the value to check if valid
	 * @return true if valid, false otherwise
	 */
	boolean isBatchSizeValid(final int batchSize) {
		if (batchSize <= 0) {
			LOGGER.error(BATCH_JOB_NAME + " Failed: batchSize must be > 0");
			return false;
		}
		
		return true;
	}

	/**
	 * Determines the time we must check order locks against, if the order locks were
	 * created before this delete them then they must be removed otherwise they are
	 * still valid order locks.
	 *
	 * @param minsToExpireLocks - the number of minutes we must look backwards in time
	 * @return the calendar with the time we must discriminate order locks against
	 */
	protected Date getOrderLockDeleteTime(final int minsToExpireLocks) {
		return DateUtils.addMinutes(getTimeService().getCurrentTime(), -minsToExpireLocks);
	}

	@Override
	public OrderLockService getOrderLockService() {
		return orderLockService;
	}

	@Override
	public void setOrderLockService(final OrderLockService orderLockService) {
		this.orderLockService = orderLockService;
	}

	@Override
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	@Override
	public TimeService getTimeService() {
		return timeService;
	}

	protected Integer getBatchSize() {
		return getBatchSizeProvider().get();
	}

	protected SettingValueProvider<Integer> getBatchSizeProvider() {
		return batchSizeProvider;
	}

	public void setBatchSizeProvider(final SettingValueProvider<Integer> batchSizeProvider) {
		this.batchSizeProvider = batchSizeProvider;
	}

	protected Integer getStaleLockThresholdMins() {
		return getStaleLockThresholdMinsProvider().get();
	}

	protected SettingValueProvider<Integer> getStaleLockThresholdMinsProvider() {
		return staleLockThresholdMinsProvider;
	}

	/**
	 * Sets a provider representing the number of minutes in the past at which locks will be considered stale.
	 *
	 * @param staleLockThresholdMinsProvider the number of minutes in the past
	 */
	public void setStaleLockThresholdMinsProvider(final SettingValueProvider<Integer> staleLockThresholdMinsProvider) {
		this.staleLockThresholdMinsProvider = staleLockThresholdMinsProvider;
	}

}
