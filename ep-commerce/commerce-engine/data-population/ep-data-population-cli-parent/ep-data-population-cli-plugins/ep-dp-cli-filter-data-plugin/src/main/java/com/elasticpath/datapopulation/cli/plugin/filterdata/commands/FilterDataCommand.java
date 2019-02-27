/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.plugin.filterdata.commands;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;

import com.elasticpath.datapopulation.cli.tool.commands.CommandAvailabilityChecker;
import com.elasticpath.datapopulation.core.DataPopulationCore;
import com.elasticpath.datapopulation.core.context.configurer.FilterActionConfiguration;

/**
 * Command to filter the data inside the data directory configured by this tool.
 * This can be used if the import of the data is to be done at a later date.
 */
public class FilterDataCommand implements CommandMarker {
	/**
	 * The name of the 'filter-data' command as accessed by the Spring Shell Command Line Interface.
	 */
	protected static final String FILTER_DATA_CLI_COMMAND = "filter-data";

	private CommandAvailabilityChecker availabilityChecker;

	@Autowired
	private DataPopulationCore dataPopulationCore;

	// Command method

	/**
	 * Returns whether the 'filter-data' command is available, delegates to the command availability checker
	 * ({@link #getAvailabilityChecker()}.
	 *
	 * @return true if the 'filter-data' command is available; false otherwise.
	 */
	@CliAvailabilityIndicator(FILTER_DATA_CLI_COMMAND)
	public boolean isFilterDataCommandAvailable() {
		return getAvailabilityChecker().isCommandAvailable(FILTER_DATA_CLI_COMMAND);
	}

	/**
	 * Command to filter the configured data directory to the given output directory.
	 * This is so that the filtered data can be processed separately at a later date. Without filtering first,
	 * the data would not be usable outside of this tool.
	 *
	 * @param outputDirectory the directory to output the filtered data to.
	 */
	@CliCommand(value = FILTER_DATA_CLI_COMMAND, help = "Filters the data in the configured data directory,"
			+ " outputting it to the given output directory.")
	public void filterDataDirectory(@CliOption(key = "output", mandatory = true,
			help = "The directory to output the filtered data to.") final File outputDirectory) {
		FilterActionConfiguration actionConfigurer = new FilterActionConfiguration();
		actionConfigurer.setFilterOutputDirectory(outputDirectory);
		dataPopulationCore.getDataPopulationContext().setActionConfiguration(actionConfigurer);
		dataPopulationCore.runActionExecutor(FILTER_DATA_CLI_COMMAND);
	}

	// Getters and Setters
	protected CommandAvailabilityChecker getAvailabilityChecker() {
		return this.availabilityChecker;
	}

	public void setAvailabilityChecker(final CommandAvailabilityChecker availabilityChecker) {
		this.availabilityChecker = availabilityChecker;
	}
}
