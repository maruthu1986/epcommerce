/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.job.descriptor.dao.impl;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.client.controller.FileSystemHelperFactory;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDao;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDaoFactory;

/**
 * Implementation of {@link JobDescriptorDaoFactory}.
 */
public class JobDescriptorDaoFactoryImpl implements JobDescriptorDaoFactory {

	private FileSystemHelperFactory fileSystemHelperFactory;
	private String jobDescriptorFileName;

	@Override
	public JobDescriptorDao createJobDescriptorDao(final SyncJobConfiguration syncJobConfiguration) {
		return new JobDescriptorDaoImpl(getJobDescriptorFileName(), getFileSystemHelperFactory().createFileSystemHelper(syncJobConfiguration));
	}

	public void setFileSystemHelperFactory(final FileSystemHelperFactory fileSystemHelperFactory) {
		this.fileSystemHelperFactory = fileSystemHelperFactory;
	}

	protected FileSystemHelperFactory getFileSystemHelperFactory() {
		return fileSystemHelperFactory;
	}

	public void setJobDescriptorFileName(final String jobDescriptorFileName) {
		this.jobDescriptorFileName = jobDescriptorFileName;
	}

	protected String getJobDescriptorFileName() {
		return jobDescriptorFileName;
	}

}
