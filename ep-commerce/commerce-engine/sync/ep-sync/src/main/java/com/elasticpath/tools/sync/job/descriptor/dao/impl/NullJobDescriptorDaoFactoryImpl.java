/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.job.descriptor.dao.impl;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDao;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDaoFactory;

/**
 * An implementation of {@link JobDescriptorDaoFactory} that produces no-op {@link JobDescriptorDao} instances.
 */
public class NullJobDescriptorDaoFactoryImpl implements JobDescriptorDaoFactory {

	@Override
	public JobDescriptorDao createJobDescriptorDao(final SyncJobConfiguration syncJobConfiguration) {
		return new NullJobDescriptorDaoImpl();
	}

	/**
	 * A no-op implementation of {@link JobDescriptorDao}.
	 */
	private static final class NullJobDescriptorDaoImpl implements JobDescriptorDao {

		@Override
		public JobDescriptor load() {
			return null;
		}

		@Override
		public void save(final JobDescriptor jobDescriptor) {
			// intentionally not implemented
		}

	}

}