/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.dao.impl;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.dao.TransactionJobDao;
import com.elasticpath.tools.sync.job.dao.TransactionJobDaoFactory;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;

/**
 * An implementation of {@link TransactionJobDaoFactory} that produces no-op {@link TransactionJobDao} instances.  Used when no save or load are
 * required.
 */
public class NullTransactionJobDaoFactoryImpl implements TransactionJobDaoFactory {

	@Override
	public TransactionJobDao createTransactionJobDao(final SyncJobConfiguration syncJobConfiguration) {
		return new NullTransactionJobDaoImpl();
	}

	/**
	 * A no-op implementation of the {@link TransactionJobDao} used when no save or load are required.
	 */
	private static final class NullTransactionJobDaoImpl implements TransactionJobDao {

		@Override
		public void load(final SerializableObjectListener listener) {
			// this implementation is supposed to do nothing
		}

		@Override
		public void save(final TransactionJob transactionJob) {
			// this implementation is supposed to do nothing
		}

	}

}