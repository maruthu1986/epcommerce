/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.job.dao.impl;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.client.controller.FileSystemHelperFactory;
import com.elasticpath.tools.sync.job.dao.TransactionJobDao;
import com.elasticpath.tools.sync.job.dao.TransactionJobDaoFactory;

/**
 * Implementation of {@link TransactionJobDaoFactory}.
 */
public class TransactionJobDaoFactoryImpl implements TransactionJobDaoFactory {

	private String jobUnitFileName;
	private FileSystemHelperFactory fileSystemHelperFactory;

	@Override
	public TransactionJobDao createTransactionJobDao(final SyncJobConfiguration syncJobConfiguration) {
		return new TransactionJobDaoImpl(getJobUnitFileName(), getFileSystemHelperFactory().createFileSystemHelper(syncJobConfiguration));
	}

	public void setJobUnitFileName(final String jobUnitFileName) {
		this.jobUnitFileName = jobUnitFileName;
	}

	protected String getJobUnitFileName() {
		return jobUnitFileName;
	}

	public void setFileSystemHelperFactory(final FileSystemHelperFactory fileSystemHelperFactory) {
		this.fileSystemHelperFactory = fileSystemHelperFactory;
	}

	protected FileSystemHelperFactory getFileSystemHelperFactory() {
		return fileSystemHelperFactory;
	}

}
