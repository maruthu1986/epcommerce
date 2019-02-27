/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tools.sync.target;

import java.util.List;

import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.merge.MergeEngine;

/**
 * Provides synchronization of a transaction unit to target environment in a single transaction.
 */
public interface SyncService {

	/**
	 * Provides synchronization of a transaction unit to target environment in a single transaction.
	 *
	 * @param transactionJobUnit transaction job unit to process
	 * @throws SyncServiceTransactionRollBackException if the transaction has been rolled back
	 */
	void processTransactionJobUnit(TransactionJobUnit transactionJobUnit) throws SyncServiceTransactionRollBackException;

	/**
	 * Processes a job entry in the outer transaction.
	 *
	 * @param jobEntry Job Entry to process.
	 * @throws SyncServiceTransactionRollBackException if the transaction has been rolled back
	 */
	void processJobEntry(JobEntry jobEntry) throws SyncServiceTransactionRollBackException;

	/**
	 * @param daoAdapterFactory the daoAdapterFactory to set
	 */
	void setDaoAdapterFactory(DaoAdapterFactory daoAdapterFactory);

	/**
	 * @param mergeEngine the mergeEngine to set
	 */
	void setMergeEngine(MergeEngine mergeEngine);

	/**
	 * Uses spring to register a set of callbacks which will be called before and after
	 * doing an update or remove for each job entry. The callbacks are synchronous and
	 * operate in the same transaction as the sync so they should complete quickly. This
	 * is intentional because we desire any changes made as part of the sync to be part of
	 * the transaction so it appears at the end, not in the middle of a potentially long
	 * transaction.
	 *
	 * @param callbacks set of callbacks to install for notifications/modifying process
	 */
	void setHookCallbacks(List<JobTransactionCallback> callbacks);

	/**
	 * Adds a hook callback.
	 *
	 * @param callback the callback to add
	 */
	void addHookCallback(JobTransactionCallback callback);
}