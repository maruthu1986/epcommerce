/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.springcli.extensions;

import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.HistoryFileNameProvider;
import org.springframework.stereotype.Component;

/**
 * Standard Data Population CLI {@link HistoryFileNameProvider} implementation.
 */
@Component
@Order(DpBannerProvider.STANDARD_DATA_POPULATION_PROVIDER_PRECEDENCE)
public class DpHistoryFilenameProvider implements HistoryFileNameProvider {
	/**
	 * The history file name returned by {@link #getHistoryFileName()}.
	 */
	protected static final String HISTORY_FILENAME = "logs/data-population.commandhistory";
	/**
	 * The name returned by {@link #name()}.
	 */
	private static final String FILENAME_PROVIDER_NAME = "Data Population CLI History Filename Provider";

	@Override
	public String name() {
		return FILENAME_PROVIDER_NAME;
	}

	@Override
	public String getHistoryFileName() {
		return HISTORY_FILENAME;
	}
}
