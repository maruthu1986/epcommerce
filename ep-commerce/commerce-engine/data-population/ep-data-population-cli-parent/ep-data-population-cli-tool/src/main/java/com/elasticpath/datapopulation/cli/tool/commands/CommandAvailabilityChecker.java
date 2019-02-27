/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.commands;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Utility class for CLI Command classes to allow them to check if their commands' availability has been configured availability.
 */
public class CommandAvailabilityChecker {
	private Collection<String> availableCommands;

	/**
	 * Checks whether any of the given commands are contained in the configured available commands ({@link #getAvailableCommands()}).
	 * If no commands are configured, then this method always returns true.
	 *
	 * @param commands the commands to check, any one of them needs to be available for this method to return true.
	 * @return true if any of the commands given are configured as available, or if {@link #isAllCommandsAvailable()} returns true; false otherwise.
	 */
	public boolean isCommandAvailable(final String... commands) {
		if (ArrayUtils.isEmpty(commands)) {
			throw new IllegalArgumentException("No command specified");
		}

		return isAllCommandsAvailable() || CollectionUtils.containsAny(getAvailableCommands(), Arrays.asList(commands));
	}

	/**
	 * Returns if all commands are available. True if the configured available commands is either null, or an empty collection.
	 *
	 * @return true if {@link #getAvailableCommands()} returns null or an empty collection; false otherwise.
	 */
	public boolean isAllCommandsAvailable() {
		return CollectionUtils.isEmpty(getAvailableCommands());
	}

	// Getters and Setters

	protected Collection<String> getAvailableCommands() {
		return this.availableCommands;
	}

	public void setAvailableCommands(final Collection<String> availableCommands) {
		this.availableCommands = availableCommands;
	}
}
