/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.plugin.confdir;

import org.springframework.core.annotation.Order;

import com.elasticpath.datapopulation.cli.tool.annotations.AfterAllGlobalCliOptions;
import com.elasticpath.datapopulation.cli.tool.annotations.DpCliComponent;
import com.elasticpath.datapopulation.cli.tool.annotations.DpCliOption;
import com.elasticpath.datapopulation.cli.tool.configuration.FileConfiguration;

/**
 * Data Population CLI Component to configure the config directory to run with.
 */
@DpCliComponent
public class ConfigDirectoryConfiguration extends FileConfiguration {
	/**
	 * The command line parameter (prepended with '--') that is used to configure the location of the config directory.
	 */
	public static final String GLOBAL_CONFIGURATION_OPTION_NAME = "configDirectory";

	/**
	 * The name of the system properties key that is set to the value of the {@link #GLOBAL_CONFIGURATION_OPTION_NAME} configuration option specified
	 * on the command line.
	 */
	public static final String CONFIG_DIRECTORY_KEY = "data.population.config.directory";

	/**
	 * The offset subtracted from {@link #SET_PROPERTY_ORDER} to be used by {@link #SET_CONFIG_DIRECTORY_PROPERTY_ORDER}.
	 * See that constant for more information on its usage.
	 */
	protected static final int SET_CONFIG_DIRECTORY_OFFSET = 25;

	/**
	 * Order value used by {@link #setProperty()}. If the config directory isn't specified we ideally want to validate after
	 * DataDirectoryConfiguration which uses the value specified by DataDirectoryConfiguration.SET_DATA_DIRECTORY_OFFSET (-30).
	 * <p>
	 * We intentionally don't couple ourselves to that class directly by referencing its order explicitly as the order is not critical
	 * and we want to avoid unnecessary coupling.
	 */
	protected static final int SET_CONFIG_DIRECTORY_PROPERTY_ORDER = SET_PROPERTY_ORDER - SET_CONFIG_DIRECTORY_OFFSET;

	/**
	 * Constructor setting up this configuration object.
	 */
	public ConfigDirectoryConfiguration() {
		super(CONFIG_DIRECTORY_KEY);
		setValueMandatory(true);
		setMustExistOnFileSystem(true);
	}

	/**
	 * Symmetrical method to {@link #setConfigDirectory(String)} which delegates its response to {@link #getFileLocation()}.
	 *
	 * @return the config directory location in use.
	 */
	public String getConfigDirectory() {
		return getFileLocation();
	}

	/**
	 * Sets the location of the config directory to use. This method is annotated so that it is set if the command line
	 * global configuration option with key {@link #GLOBAL_CONFIGURATION_OPTION_NAME} is specified.
	 *
	 * @param environmentDirectoryLocation the config directory location to use.
	 */
	@DpCliOption(key = GLOBAL_CONFIGURATION_OPTION_NAME, hasArgument = true,
			help = "Specifies the location of the config directory to use.")
	public void setConfigDirectory(final String environmentDirectoryLocation) {
		setFileLocation(environmentDirectoryLocation);
	}

	/**
	 * Method overridden to provide a different {@link Order} annotation value than the default one specified by the super class' implementation.
	 * Uses {@link #SET_CONFIG_DIRECTORY_PROPERTY_ORDER} for its {@link Order} value. See that constant for more information.
	 */
	@Override
	@AfterAllGlobalCliOptions
	@Order(SET_CONFIG_DIRECTORY_PROPERTY_ORDER)
	public void setProperty() {
		super.setProperty();
	}

	@Override
	protected String getGlobalConfigurationOptionName() {
		return GLOBAL_CONFIGURATION_OPTION_NAME;
	}

	/**
	 * Returns the config directory passed in, since that is the directory this object is configuring.
	 *
	 * @param dataDirectory   the data directory in use by the system.
	 * @param configDirectory the environment directory in use by the system.
	 * @return configDirectory.
	 */
	@Override
	protected String getFileLocation(final String dataDirectory, final String configDirectory) {
		return configDirectory;
	}
}
