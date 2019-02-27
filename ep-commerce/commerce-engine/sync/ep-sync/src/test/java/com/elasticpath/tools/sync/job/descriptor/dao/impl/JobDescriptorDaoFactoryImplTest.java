/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.job.descriptor.dao.impl;

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
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDao;

/**
 * Test class for {@link JobDescriptorDaoFactoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class JobDescriptorDaoFactoryImplTest {

	@Mock
	private SyncJobConfiguration syncJobConfiguration;

	@Mock
	private FileSystemHelperFactory fileSystemHelperFactory;

	@InjectMocks
	private JobDescriptorDaoFactoryImpl factory;

	@Test
	public void verifyDaoConstructedWithNewFileHelperInstance() throws Exception {
		final FileSystemHelper fileSystemHelper = mock(FileSystemHelper.class);

		given(fileSystemHelperFactory.createFileSystemHelper(syncJobConfiguration))
				.willReturn(fileSystemHelper);

		final JobDescriptorDao jobDescriptorDao = factory.createJobDescriptorDao(syncJobConfiguration);

		assertThat(jobDescriptorDao)
				.isInstanceOf(JobDescriptorDaoImpl.class);

		assertThat(((JobDescriptorDaoImpl) jobDescriptorDao).getFileSystemHelper())
				.isSameAs(fileSystemHelper);
	}

	@Test
	public void verifyDaoConstructedWithConfiguredJobUnitName() throws Exception {
		final String jobDescriptorFileName = "JOBUNIT.123.dat";

		factory.setJobDescriptorFileName(jobDescriptorFileName);

		final JobDescriptorDao jobDescriptorDao = factory.createJobDescriptorDao(syncJobConfiguration);

		assertThat(jobDescriptorDao)
				.isInstanceOf(JobDescriptorDaoImpl.class);

		assertThat(((JobDescriptorDaoImpl) jobDescriptorDao).getJobDescriptorFileName())
				.isEqualTo(jobDescriptorFileName);
	}

}