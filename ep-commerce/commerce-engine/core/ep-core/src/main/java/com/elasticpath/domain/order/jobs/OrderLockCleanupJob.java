/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.order.jobs;

import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderLockService;

/**
 * Job which will remove order locks that have expired past a defined amount of time, which
 * is determined by a setting.
 */
public interface OrderLockCleanupJob {

	/**
	 * Cleans up the order locks that have been created before a certain amount of time, the
	 * amount of time is defined by a system setting.
	 * @return the number of order locks removed
	 */
	OrderLockCleanupResult cleanUpOrderLocks();

	/**
	 * Returns the order lock service.
	 *
	 * @return the order lock service
	 */
	OrderLockService getOrderLockService();

	/**
	 * Sets the order lock service.
	 *
	 * @param orderLockService - the order lock service to set
	 */
	void setOrderLockService(OrderLockService orderLockService);

	/**
	 * @param timeService the timeService to set
	 */
	void setTimeService(TimeService timeService);

	/**
	 * @return the timeService
	 */
	TimeService getTimeService();
}
