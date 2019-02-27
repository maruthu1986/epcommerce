/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Launches a runnable task after specified delay. If a new task had been scheduled before the task in the queue has been executed, the queue
 * gets cleared and a new task is being placed into the queue. The delay also gets reset to zero. 
 */
public class RepeatableDelayedTask {

	private final long delayMillis;
	private final ScheduledThreadPoolExecutor validationExecutor = new ScheduledThreadPoolExecutor(1);
	private final Runnable task;

	/**
	 * Constructor.
	 * 
	 * @param task task
	 * @param millisDelay delay
	 */
	public RepeatableDelayedTask(final Runnable task, final long millisDelay) {
		this.task = task;
		this.delayMillis = millisDelay;
	}

	/**
	 * Schedules the task for execution.
	 */
	public void schedule() {
		validationExecutor.getQueue().clear();

		validationExecutor.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
	}
}
