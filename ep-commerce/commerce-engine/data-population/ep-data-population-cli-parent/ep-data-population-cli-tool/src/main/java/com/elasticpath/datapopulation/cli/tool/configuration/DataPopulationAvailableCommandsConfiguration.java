/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.configuration;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.elasticpath.datapopulation.cli.tool.DataPopulationCliException;
import com.elasticpath.datapopulation.core.utils.DpUtils;

/**
 * Class that configures the commands that should be available on the command line interface based on an injected configuration value,
 * typically from the data-population.properties file.
 */
public class DataPopulationAvailableCommandsConfiguration {
	private static final Logger LOG = Logger.getLogger(DataPopulationAvailableCommandsConfiguration.class);

	private String availableCommandsString;
	private String unavailableCommandsString;

	private Set<String> availableCommands;

	/**
	 * Parses the available commands by calling {@link #parseAvailableCommands()} to parse the ones set by {@link #getAvailableCommandsString()}
	 * and remove the ones included in {@link #getUnavailableCommandsString()}, and then finally calls {@link #setAvailableCommands(java.util.Set)}
	 * with the parsed results. This then allows the Set contained in {@link #getAvailableCommands()} to be injected into objects that require it
	 * such as the {@link com.elasticpath.datapopulation.cli.tool.commands.CommandAvailabilityChecker} object.
	 * See the Spring wiring of this class for more information.
	 */
	public void parseAndSetAvailableCommands() {
		setAvailableCommands(parseAvailableCommands());
	}

	/**
	 * Parses the available commands in the String provided by {@link #getAvailableCommandsString()} and removes the ones included in
	 * {@link #getUnavailableCommandsString()} if any are set.
	 *
	 * @return a {@link Set} of available commands with any unavailable commands removed, never null.
	 */
	protected Set<String> parseAvailableCommands() {
		Set<String> result = parseCommands(getAvailableCommandsString());

		if (CollectionUtils.isNotEmpty(result)) {
			final Set<String> unavailableCommands = parseCommands(getUnavailableCommandsString());
			removeUnavailableCommands(result, unavailableCommands, true);
		}

		return result;
	}

	/**
	 * Parses the given available commands string (as comma-separated command names) and returns them as a {@link Set}.
	 *
	 * @param commandsString the available commands string to parse.
	 * @return a {@link Set} of available commands, always non-null.
	 */
	protected Set<String> parseCommands(final String commandsString) {
		final Set<String> result = new TreeSet<>();

		if (StringUtils.isNotBlank(commandsString)) {
			try {
				parseAvailableCommands(new StringReader(commandsString), result);
			} catch (final IOException e) {
				throw new DataPopulationCliException("Error: Unable to parse the configured commands from value: "
						+ commandsString + ". " + DpUtils.getNestedExceptionMessage(e), e);
			}
		}

		return result;
	}

	/**
	 * Processes a {@link Reader} which contains the comma-separated available commands to parse, and adds the results to the given
	 * {@link Collection}.
	 *
	 * @param availableCommandsReader the reader to read the comma-separated available commands from.
	 * @param destination             the collection to update with the parsed command names.
	 * @throws IOException if there is a problem reading from the {@link Reader}.
	 */
	protected void parseAvailableCommands(final Reader availableCommandsReader, final Collection<String> destination) throws IOException {
		final CsvListReader reader = createAvailableCommandsReader(availableCommandsReader);
		DpUtils.readAllEntries(reader, destination);
	}

	/**
	 * Removes all commands to remove from the available commands given, warning if any are not found if warnIfNotPresent is true.
	 *
	 * @param availableCommands the collection to remove from.
	 * @param commandsToRemove  the collection of commands to remove.
	 * @param warnIfNotPresent  true if a logged warning should be displayed for each command to remove that isn't present in availableCommands.
	 */
	protected void removeUnavailableCommands(final Collection<String> availableCommands, final Collection<String> commandsToRemove,
											 final boolean warnIfNotPresent) {
		if (CollectionUtils.isNotEmpty(availableCommands) && CollectionUtils.isNotEmpty(commandsToRemove)) {
			if (warnIfNotPresent) {
				for (String commandToRemove : commandsToRemove) {
					if (!availableCommands.remove(commandToRemove)) {
						LOG.warn("Unavailable command '" + commandToRemove
								+ "' is not present in the available commands, so cannot be removed. Available commands: " + availableCommands);
					}
				}
			} else {
				availableCommands.removeAll(commandsToRemove);
			}
		}
	}

	// Factory methods

	/**
	 * Creates and returns a {@link CsvListReader} object for the given {@link Reader}. This {@link CsvListReader} is the delegate
	 * object which will do the parsing.
	 *
	 * @param availableCommandsReader the reader to read and parse its contents.
	 * @return a {@link CsvListReader} which will do the parsing.
	 */
	protected CsvListReader createAvailableCommandsReader(final Reader availableCommandsReader) {
		final CsvPreference.Builder csvPreferenceBuilder = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE);
		csvPreferenceBuilder.surroundingSpacesNeedQuotes(true);

		return new CsvListReader(availableCommandsReader, csvPreferenceBuilder.build());
	}

	// Getters and Setters

	protected String getAvailableCommandsString() {
		return this.availableCommandsString;
	}

	public void setAvailableCommandsString(final String availableCommandsString) {
		this.availableCommandsString = availableCommandsString;
	}

	protected String getUnavailableCommandsString() {
		return this.unavailableCommandsString;
	}

	public void setUnavailableCommandsString(final String unavailableCommandsString) {
		this.unavailableCommandsString = unavailableCommandsString;
	}

	public Set<String> getAvailableCommands() {
		return this.availableCommands;
	}

	public void setAvailableCommands(final Set<String> availableCommands) {
		this.availableCommands = availableCommands;
	}
}
