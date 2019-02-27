/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.job.dao;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;

/**
 * Creates new {@link TransactionJobDao} instances.
 */
public interface TransactionJobDaoFactory {

	/**
	 * Creates a new {@link TransactionJobDao} instance.
	 *
	 * @param syncJobConfiguration the configuration of the current sync job
	 * @return a new TransactionJobDao instance
	 */
	TransactionJobDao createTransactionJobDao(SyncJobConfiguration syncJobConfiguration);

}
