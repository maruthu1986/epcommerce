/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.client;

import com.elasticpath.tools.sync.target.result.Summary;

/**
 * The launcher starts the sync tool process and provides the result summary.
 */
public interface SyncToolLauncher {

	/**
	 * Executes the synchronization process.
	 *
	 * @param jobConfiguration the individual job configuration
	 * @return the result summary of the sync process
	 * @throws com.elasticpath.tools.sync.exception.SyncToolConfigurationException if a sync tool configuration exception occurs
	 */
	Summary processJob(SyncJobConfiguration jobConfiguration);

}
