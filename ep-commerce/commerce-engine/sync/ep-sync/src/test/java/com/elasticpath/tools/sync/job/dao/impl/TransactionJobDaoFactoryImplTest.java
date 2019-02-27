/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.job.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.client.controller.FileSystemHelper;
import com.elasticpath.tools.sync.client.controller.FileSystemHelperFactory;
import com.elasticpath.tools.sync.job.dao.TransactionJobDao;

/**
 * Test class for {@link TransactionJobDaoFactoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionJobDaoFactoryImplTest {

	@Mock
	private SyncJobConfiguration syncJobConfiguration;

	@Mock
	private FileSystemHelperFactory fileSystemHelperFactory;

	@InjectMocks
	private TransactionJobDaoFactoryImpl factory;

	@Test
	public void verifyDaoConstructedWithNewFileHelperInstance() throws Exception {
		final FileSystemHelper fileSystemHelper = mock(FileSystemHelper.class);

		given(fileSystemHelperFactory.createFileSystemHelper(syncJobConfiguration))
				.willReturn(fileSystemHelper);

		final TransactionJobDao transactionJobDao = factory.createTransactionJobDao(syncJobConfiguration);

		assertThat(transactionJobDao)
				.isInstanceOf(TransactionJobDaoImpl.class);

		assertThat(((TransactionJobDaoImpl) transactionJobDao).getFileSystemHelper())
				.isSameAs(fileSystemHelper);
	}

	@Test
	public void verifyDaoConstructedWithConfiguredJobUnitName() throws Exception {
		final String jobUnitFileName = "JOBUNIT.123.dat";

		factory.setJobUnitFileName(jobUnitFileName);

		final TransactionJobDao transactionJobDao = factory.createTransactionJobDao(syncJobConfiguration);

		assertThat(transactionJobDao)
				.isInstanceOf(TransactionJobDaoImpl.class);

		assertThat(((TransactionJobDaoImpl) transactionJobDao).getJobUnitFileName())
				.isEqualTo(jobUnitFileName);
	}

}