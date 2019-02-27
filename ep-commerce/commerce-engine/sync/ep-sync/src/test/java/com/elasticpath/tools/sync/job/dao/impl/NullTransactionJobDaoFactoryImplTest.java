/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.job.dao.impl;

import org.junit.Test;

import com.elasticpath.tools.sync.job.dao.TransactionJobDao;

/**
 * Test class for {@link NullTransactionJobDaoFactoryImpl}.
 */
public class NullTransactionJobDaoFactoryImplTest {

	private final NullTransactionJobDaoFactoryImpl factory = new NullTransactionJobDaoFactoryImpl();

	@Test
	public void verifyCreateCreatesNoOpDao() throws Exception {
		final TransactionJobDao dao = factory.createTransactionJobDao(null);

		dao.load(null);
		dao.save(null);
	}

}