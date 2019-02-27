/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.descriptor.dao.impl;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.client.controller.FileSystemHelper;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDao;

/**
 * An implementation of the {@link JobDescriptorDao} that saves and loads
 * a {@link JobDescriptor} to/from a file.
 */
public class JobDescriptorDaoImpl implements JobDescriptorDao {

	private static final Logger LOG = Logger.getLogger(JobDescriptorDaoImpl.class);

	private final String jobDescriptorFileName;

	private final FileSystemHelper fileSystemHelper;

	/**
	 * Constructor.
	 *
	 * @param jobDescriptorFileName the job descriptor file name
	 * @param fileSystemHelper the file system helper
	 */
	public JobDescriptorDaoImpl(final String jobDescriptorFileName, final FileSystemHelper fileSystemHelper) {
		this.jobDescriptorFileName = jobDescriptorFileName;
		this.fileSystemHelper = fileSystemHelper;
	}

	@Override
	public void save(final JobDescriptor jobDescriptor) {
		getFileSystemHelper().saveJobDescriptor(jobDescriptor, getJobDescriptorFileName());
		LOG.debug(getJobDescriptorFileName() + " has been saved");
	}

	@Override
	public JobDescriptor load() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the fileSystemHelper
	 */
	protected FileSystemHelper getFileSystemHelper() {
		return fileSystemHelper;
	}

	/**
	 * @return the jobDescriptorFileName
	 */
	protected String getJobDescriptorFileName() {
		return jobDescriptorFileName;
	}

}
