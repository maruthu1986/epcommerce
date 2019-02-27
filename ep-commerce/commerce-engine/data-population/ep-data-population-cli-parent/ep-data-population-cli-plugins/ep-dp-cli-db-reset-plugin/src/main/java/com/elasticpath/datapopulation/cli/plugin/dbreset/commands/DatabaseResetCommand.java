/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.plugin.dbreset.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;

import com.elasticpath.datapopulation.cli.tool.commands.CommandAvailabilityChecker;
import com.elasticpath.datapopulation.core.DataPopulationCore;

/**
 * Command to reset the database: dropping, creating, and updating the database. This command invokes lower-level commands to perform those tasks.
 */
public class DatabaseResetCommand implements CommandMarker {
	/**
	 * The name of the 'reset database' command as accessed by the Spring Shell Command Line Interface.
	 */
	protected static final String RESET_DATABASE_CLI_COMMAND = "reset-db";

	private CommandAvailabilityChecker availabilityChecker;

	@Autowired
	private DataPopulationCore dataPopulationCore;

	// Command method

	/**
	 * Returns whether the 'reset database' command is available, delegates to the command availability checker
	 * ({@link #getAvailabilityChecker()}.
	 *
	 * @return true if the 'reset database' command is available; false otherwise.
	 */
	@CliAvailabilityIndicator(RESET_DATABASE_CLI_COMMAND)
	public boolean isResetDatabaseCommandAvailable() {
		return getAvailabilityChecker().isCommandAvailable(RESET_DATABASE_CLI_COMMAND);
	}

	/**
	 * Command to reset the database, this command actually calls three sub-commands to accomplish that.
	 * <ol>
	 * <li>Initialize Database command to drop and recreate the database;</li>
	 * <li>Initialize Schema command to create the tables etc in the database;</li>
	 * <li>Update Database command to invoke Liquibase/Import-Export to run all outstanding database updates.</li>
	 * </ol>
	 */
	@CliCommand(value = RESET_DATABASE_CLI_COMMAND, help = "Resets the database by invoking the following commands in order: "
			+ "'initialize database', 'initialize schema', 'update database'.")
	public void resetDatabase() {
		dataPopulationCore.runActionExecutor(RESET_DATABASE_CLI_COMMAND);
	}

	// Getters and Setters
	protected CommandAvailabilityChecker getAvailabilityChecker() {
		return this.availabilityChecker;
	}

	public void setAvailabilityChecker(final CommandAvailabilityChecker availabilityChecker) {
		this.availabilityChecker = availabilityChecker;
	}
}
