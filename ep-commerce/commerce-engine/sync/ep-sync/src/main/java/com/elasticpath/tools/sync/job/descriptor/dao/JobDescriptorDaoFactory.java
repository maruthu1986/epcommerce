/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.job.descriptor.dao;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;

/**
 * Creates new {@link JobDescriptorDao} instances.
 */
public interface JobDescriptorDaoFactory {

	/**
	 * Creates a new {@link JobDescriptorDao} instance.
	 *
	 * @param syncJobConfiguration the configuration of the current sync job
	 * @return a new JobDescriptorDao instance
	 */
	JobDescriptorDao createJobDescriptorDao(SyncJobConfiguration syncJobConfiguration);

}
