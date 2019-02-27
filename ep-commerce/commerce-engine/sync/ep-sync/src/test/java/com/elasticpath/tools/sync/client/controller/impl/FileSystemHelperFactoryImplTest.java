/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.client.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.client.controller.FileSystemHelper;

/**
 * Test class for {@link FileSystemHelperFactoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FileSystemHelperFactoryImplTest {

	@Mock
	private SyncJobConfiguration syncJobConfig;

	@InjectMocks
	private FileSystemHelperFactoryImpl factory;

	@Test
	public void verifyCreateCreatesNewInstances() throws Exception {
		final FileSystemHelper fileSystemHelper1 = factory.createFileSystemHelper(syncJobConfig);
		final FileSystemHelper fileSystemHelper2 = factory.createFileSystemHelper(syncJobConfig);

		assertThat(fileSystemHelper1)
				.isNotSameAs(fileSystemHelper2);
	}

}