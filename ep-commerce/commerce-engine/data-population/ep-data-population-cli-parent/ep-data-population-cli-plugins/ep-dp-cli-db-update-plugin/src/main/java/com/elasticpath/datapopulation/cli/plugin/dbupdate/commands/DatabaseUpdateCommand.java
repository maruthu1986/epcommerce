/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.plugin.dbupdate.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;

import com.elasticpath.datapopulation.cli.tool.commands.CommandAvailabilityChecker;
import com.elasticpath.datapopulation.core.DataPopulationCore;

/**
 * Command to update the database using the data stored in the injected data directory.
 */
public class DatabaseUpdateCommand implements CommandMarker {

	/**
	 * The name of the 'update database' command as accessed by the Spring Shell Command Line Interface.
	 */
	protected static final String UPDATE_DATABASE_CLI_COMMAND = "update-db";

	private CommandAvailabilityChecker availabilityChecker;

	@Autowired
	private DataPopulationCore dataPopulationCore;

	// Command method

	/**
	 * Returns whether the 'update database' command is available, delegates to the command availability checker ({@link #getAvailabilityChecker()}.
	 *
	 * @return true if the 'update database' command is available; false otherwise.
	 */
	@CliAvailabilityIndicator(UPDATE_DATABASE_CLI_COMMAND)
	public boolean isUpdateDatabaseCommandAvailable() {
		return getAvailabilityChecker().isCommandAvailable(UPDATE_DATABASE_CLI_COMMAND);
	}

	/**
	 * Updates the database by first filtering the data files for the configured environment, and then invoking the LiquibaseService
	 * to execute the filtered Liquibase changelog file.
	 */
	@CliCommand(value = UPDATE_DATABASE_CLI_COMMAND,
			help = "Uses the data directory configured to update the database through Liquibase and Import/Export.")
	public void updateDatabase() {
		dataPopulationCore.runActionExecutor(UPDATE_DATABASE_CLI_COMMAND);
	}

	// Getters and Setters
	protected CommandAvailabilityChecker getAvailabilityChecker() {
		return this.availabilityChecker;
	}

	public void setAvailabilityChecker(final CommandAvailabilityChecker availabilityChecker) {
		this.availabilityChecker = availabilityChecker;
	}
}
