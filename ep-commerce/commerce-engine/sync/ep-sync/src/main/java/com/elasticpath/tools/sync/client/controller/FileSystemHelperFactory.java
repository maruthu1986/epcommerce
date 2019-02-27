/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.client.controller;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;

/**
 * Factory for creating {@link FileSystemHelper} instances.
 */
public interface FileSystemHelperFactory {

	/**
	 * Creates {@link FileSystemHelper} instances.
	 *
	 * @param syncJobConfiguration the configuration for the current sync job
	 * @return a new FileSystemHelper instance
	 */
	FileSystemHelper createFileSystemHelper(SyncJobConfiguration syncJobConfiguration);

}
