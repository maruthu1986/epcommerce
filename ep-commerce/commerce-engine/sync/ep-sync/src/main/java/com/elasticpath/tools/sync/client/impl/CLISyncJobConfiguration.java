/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.client.impl;

import com.elasticpath.tools.sync.client.SynchronizationTool;

/**
 * <code>{@link SyncJobConfiguration}</code> to be used by the Command Line Interface.
 */
public class CLISyncJobConfiguration extends SyncJobConfigurationImpl {

	/**
	 * Constructs a new command line interface <code>{@link SyncJobConfiguration}</code>.
	 *
	 * @param commandLineConfiguration Command line configurations to be used in the construction of the job configuration.
	 * @param adapterParameter         The '-P' parameter value.
	 */
	public CLISyncJobConfiguration(final SynchronizationTool.CommandLineConfiguration commandLineConfiguration,
			final String adapterParameter) {
		super(commandLineConfiguration.getRootPath(), commandLineConfiguration.getSubDir(), adapterParameter);
	}




}
