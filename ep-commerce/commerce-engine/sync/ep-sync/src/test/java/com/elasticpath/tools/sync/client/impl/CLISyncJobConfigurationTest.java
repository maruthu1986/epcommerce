/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.client.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.client.SynchronizationTool;

/**
 * Unit test for class <code>{@link CLISyncJobConfiguration}</code>.
 */
@RunWith(MockitoJUnitRunner.class)
public class CLISyncJobConfigurationTest {

	private static final String ROOT_PATH = "/root/path";
	private static final String SUB_DIR = "/sub/dir";
	private static final boolean HAS_SUB_DIR = true;
	private static final String SOME_PARAMETER = "some parameter";

	@Test
	public void testConstruction() {
		// Given
		SynchronizationTool.CommandLineConfiguration commandLineConfiguration = givenSimpleCommandLineConfiguration(HAS_SUB_DIR);

		// When
		SyncJobConfiguration jobConfiguration = new CLISyncJobConfiguration(commandLineConfiguration, SOME_PARAMETER);

		// Then
		assertThat(jobConfiguration)
				.hasFieldOrPropertyWithValue("rootPath", ROOT_PATH)
				.hasFieldOrPropertyWithValue("subDir", SUB_DIR)
				.hasFieldOrPropertyWithValue("adapterParameter", SOME_PARAMETER);
	}

	@Test
	public void testToString() {
		// Given
		SynchronizationTool.CommandLineConfiguration commandLineConfiguration = givenSimpleCommandLineConfiguration(HAS_SUB_DIR);

		// When
		SyncJobConfiguration jobConfiguration = new CLISyncJobConfiguration(commandLineConfiguration, SOME_PARAMETER);

		// Then
		String configString = "CLISyncJobConfiguration[Job parameter=some parameter,"
				+ "Job Root Directory=/root/path,Job Sub Directory=/sub/dir]";

		assertThat(jobConfiguration).hasToString(configString);
	}

	@Test
	public void testToStringWithoutSubDir() {
		// Given
		SynchronizationTool.CommandLineConfiguration commandLineConfiguration = givenSimpleCommandLineConfiguration(!HAS_SUB_DIR);

		// When
		SyncJobConfiguration jobConfiguration = new CLISyncJobConfiguration(commandLineConfiguration, SOME_PARAMETER);

		// Then
		String configString = "CLISyncJobConfiguration[Job parameter=some parameter,Job Root Directory=/root/path]";

		assertThat(jobConfiguration).hasToString(configString);
	}

	private SynchronizationTool.CommandLineConfiguration givenSimpleCommandLineConfiguration(final boolean hasSubDir) {
		SynchronizationTool.CommandLineConfiguration commandLineConfiguration = mock(SynchronizationTool.CommandLineConfiguration.class);
		given(commandLineConfiguration.getRootPath()).willReturn(ROOT_PATH);
		if (hasSubDir) {
			given(commandLineConfiguration.getSubDir()).willReturn(SUB_DIR);
		}
		return commandLineConfiguration;
	}

}
