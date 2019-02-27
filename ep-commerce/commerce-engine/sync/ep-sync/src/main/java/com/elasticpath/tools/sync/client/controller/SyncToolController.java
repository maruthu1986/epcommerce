/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.client.controller;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * Serves to process synchronization.
 */
public interface SyncToolController {

	/**
	 * Initializes the controller. This should be called once before one or more {@link #synchronize(SyncJobConfiguration)} invocations are made.
	 */
	void startUp();

	/**
	 * Processes synchronization for a particular job. Can be called multiple times in serial (not parallel).
	 *
	 * @param jobConfiguration the job configuration to process.
	 * @return Summary instance with success and error items
	 */
	Summary synchronize(SyncJobConfiguration jobConfiguration);

	/**
	 * Shuts down the controller. This should be called once after all {@link #synchronize(SyncJobConfiguration)} invocations are made.
	 */
	void shutDown();

}
