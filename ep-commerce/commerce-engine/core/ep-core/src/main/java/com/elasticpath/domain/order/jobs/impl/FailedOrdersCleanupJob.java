/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.domain.order.jobs.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * This class removes Orders which have an OrderStatus of FAILED.
 * Which Orders it removes and how many to do at once are controlled by settings in TSettingDefinition.
 */
public class FailedOrdersCleanupJob {
	
	private static final Logger LOG = Logger.getLogger(FailedOrdersCleanupJob.class);
	private OrderService orderService;
	private TimeService timeService;
	private SettingValueProvider<Integer> batchSizeProvider;
	private SettingValueProvider<Integer> maxDaysHistoryProvider;

	/**
	 * Removes failed Orders.
	 * @return The total number of failed Orders that were removed.
	 */
	public int removeFailedOrders() {
		final long startTime = System.currentTimeMillis();
		LOG.debug("Start remove failed orders quartz job at: " + new Date(startTime));
		
		final Date removalDate = getCandidateRemovalDate();
		if (removalDate == null) {
			throw new EpServiceException("removalDate must be supplied.");
		}
		final int maxResults = getBatchSize();
		
		LOG.debug("Starting failed Orders cleanup job.");
		List<Long> failedOrderUids = getOrderService().getFailedOrderUids(removalDate, maxResults);
		getOrderService().deleteOrders(failedOrderUids);

		int removed = failedOrderUids.size();
		LOG.debug(String.format("Finished failed Orders cleanup job. Removed %d Orders.", removed));
		LOG.debug("Remove failed orders quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
		return removed;
	}

	/**
	 * @return The candidate removal date.
	 */
	protected Date getCandidateRemovalDate() {
		final int days = getMaxDaysHistoryProvider().get();
		return DateUtils.addDays(getTimeService().getCurrentTime(), -days);
	}

	/**
	 * @return The batch size.
	 */
	protected int getBatchSize() {
		return getBatchSizeProvider().get();
	}

	/**
	 * @param timeService The time service.
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * @return The time service.
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * @param orderService The OrderService.
	 */
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * @return The OrderService.
	 */
	protected OrderService getOrderService() {
		return orderService;
	}

	protected SettingValueProvider<Integer> getBatchSizeProvider() {
		return batchSizeProvider;
	}

	public void setBatchSizeProvider(final SettingValueProvider<Integer> batchSizeProvider) {
		this.batchSizeProvider = batchSizeProvider;
	}

	protected SettingValueProvider<Integer> getMaxDaysHistoryProvider() {
		return maxDaysHistoryProvider;
	}

	public void setMaxDaysHistoryProvider(final SettingValueProvider<Integer> maxDaysHistoryProvider) {
		this.maxDaysHistoryProvider = maxDaysHistoryProvider;
	}

}
