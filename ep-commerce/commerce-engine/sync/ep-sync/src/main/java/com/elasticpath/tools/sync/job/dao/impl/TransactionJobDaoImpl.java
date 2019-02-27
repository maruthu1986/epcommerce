/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.dao.impl;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.client.controller.FileSystemHelper;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.dao.TransactionJobDao;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;

/**
 * The default, file system based, DAO implementation.
 */
public class TransactionJobDaoImpl implements TransactionJobDao {

	private static final Logger LOG = Logger.getLogger(TransactionJobDaoImpl.class);

	private final String jobUnitFileName;

	private final FileSystemHelper fileSystemHelper;

	/**
	 * Constructor.
	 *
	 * @param jobUnitFileName the job unit filename
	 * @param fileSystemHelper the file system helper
	 */
	public TransactionJobDaoImpl(final String jobUnitFileName, final FileSystemHelper fileSystemHelper) {
		this.jobUnitFileName = jobUnitFileName;
		this.fileSystemHelper = fileSystemHelper;
	}

	@Override
	public void save(final TransactionJob transactionJob) {
		fileSystemHelper.saveTransactionJobToFile(transactionJob, getJobUnitFileName());
		LOG.debug(getJobUnitFileName() + " has been saved");
	}

	@Override
	public void load(final SerializableObjectListener objectListener) {
		fileSystemHelper.readTransactionJobFromFile(jobUnitFileName, objectListener);
	}

	/**
	 *
	 * @return the fileSystemHelper
	 */
	protected FileSystemHelper getFileSystemHelper() {
		return fileSystemHelper;
	}

	/**
	 *
	 * @return the jobUnitFileName
	 */
	protected String getJobUnitFileName() {
		return jobUnitFileName;
	}

}
