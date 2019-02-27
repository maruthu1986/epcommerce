/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.domain.customer.impl;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.elasticpath.service.customer.AnonymousCustomerCleanupService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Job to purge anonymous customers.
 */
public class AnonymousCustomerCleanupJob {

	private static final Logger LOG = Logger.getLogger(AnonymousCustomerCleanupJob.class);

	private TimeService timeService;
	private AnonymousCustomerCleanupService anonymousCustomerCleanupService;

	private SettingValueProvider<Integer> batchSizeProvider;
	private SettingValueProvider<Integer> maxDaysHistoryProvider;

	/**
	 * Purge the anonymous customers.<br>
	 * This will remove all customer records that have a last creation date older than the number of days specified by a system setting defined by
	 * <code>ANONYMOUS_CUSTOMER_MAX_HISTORY</code>. <br>
	 * It will also cap the number of anonymous customers that it will delete to the setting defined in
	 * <code>ANONYMOUS_CUSTOMER_BATCH_SIZE</code>.<br>
	 * (e.g. If <code>ANONYMOUS_CUSTOMER_BATCH_SIZE</code> is set to 1000, then no more than 1000 anonymous customers are cleaned up in one go).
	 *
	 * @return the total number of anonymous customers deleted
	 */
	public int purgeAnonymousCustomers() {
		final long startTime = System.currentTimeMillis();
		LOG.debug("Start purge of customer cleanup quartz job at: " + new Date(startTime));

		final Date removalDate = getCandidateRemovalDate();
		final int maxResults = getBatchSize();

		LOG.debug("Starting customer cleanup job...");
		final int removedAnonymousCustomers = getAnonymousCustomerCleanupService().deleteAnonymousCustomers(removalDate, maxResults);
		LOG.debug(String.format("Finished customer cleanup job. Removed %d Customers.", removedAnonymousCustomers));

		LOG.debug("Purge customer cleanup quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
		return removedAnonymousCustomers;
	}

	/**
	 * Gets the candidate removal date.
	 *
	 * @return the candidate removal date
	 */
	protected Date getCandidateRemovalDate() {
		final int days = getMaxDaysHistoryProvider().get();
		return DateUtils.addDays(getTimeService().getCurrentTime(), -days);
	}

	/**
	 * Gets the batch size.
	 *
	 * @return the batch size
	 */
	protected int getBatchSize() {
		return getBatchSizeProvider().get();
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	protected TimeService getTimeService() {
		return timeService;
	}

	public void setAnonymousCustomerCleanupService(final AnonymousCustomerCleanupService anonymousCustomerCleanupService) {
		this.anonymousCustomerCleanupService = anonymousCustomerCleanupService;
	}

	protected AnonymousCustomerCleanupService getAnonymousCustomerCleanupService() {
		return anonymousCustomerCleanupService;
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
