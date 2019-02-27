/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tools.sync.client.controller.impl;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.job.dao.TransactionJobDao;
import com.elasticpath.tools.sync.job.dao.TransactionJobDaoFactory;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;
import com.elasticpath.tools.sync.processing.SyncJobObjectProcessor;

/**
 * The LoadController loads data from a TransactionJob. 
 * It does only use the target system. It synchronizes by deploying the data to the target system.
 */
public class LoadController extends AbstractSyncController {
	
	private static final Logger LOG = Logger.getLogger(LoadController.class);

	private TransactionJobDaoFactory transactionJobDaoFactory;

	private SyncJobObjectProcessor objectProcessor;

	/**
	 * Reads TransactionJob from default file.
	 *
	 * @param listener the object listener
	 * @param syncJobConfiguration the configuration for the sync job
	 */
	@Override
	protected void loadTransactionJob(final SerializableObjectListener listener, final SyncJobConfiguration syncJobConfiguration) {
		LOG.debug("Loading transaction job...");
		getTransactionJobDao(syncJobConfiguration).load(listener);
	}

	/**
	 *
	 * @return the object processor instance
	 */
	@Override
	protected SyncJobObjectProcessor getObjectProcessor() {
		return objectProcessor;
	}

	/**
	 *
	 * @param objectProcessor the objectProcessor to set
	 */
	public void setObjectProcessor(final SyncJobObjectProcessor objectProcessor) {
		this.objectProcessor = objectProcessor;
	}

	/**
	 * Creates a new {@link TransactionJobDao} instance.
	 *
	 * @param syncJobConfiguration the job configuration being synchronized
	 * @return the TransactionJobDao
	 */
	protected TransactionJobDao getTransactionJobDao(final SyncJobConfiguration syncJobConfiguration) {
		return getTransactionJobDaoFactory().createTransactionJobDao(syncJobConfiguration);
	}

	protected TransactionJobDaoFactory getTransactionJobDaoFactory() {
		return transactionJobDaoFactory;
	}

	public void setTransactionJobDaoFactory(final TransactionJobDaoFactory transactionJobDaoFactory) {
		this.transactionJobDaoFactory = transactionJobDaoFactory;
	}

	/**
	 * Initializes only the target system.
	 * 
	 * @param sourceSystem the source system configuration
	 * @param targetSystem the target system configuration
	 */
	@Override
	protected void initConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
		targetSystem.initSystem();
	}

	/**
	 * Call destroy cleanup on just the target system.
	 *
	 * @param sourceSystem the source system
	 * @param targetSystem the target system
	 */
	@Override
	protected void destroyConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
		targetSystem.destroySystem();
	}
}
