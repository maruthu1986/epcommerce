/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.plugin.datadir;

import org.springframework.core.annotation.Order;

import com.elasticpath.datapopulation.cli.tool.annotations.AfterAllGlobalCliOptions;
import com.elasticpath.datapopulation.cli.tool.annotations.DpCliComponent;
import com.elasticpath.datapopulation.cli.tool.annotations.DpCliOption;
import com.elasticpath.datapopulation.cli.tool.configuration.FileConfiguration;

/**
 * Data Population CLI Component to configure the data directory to run with.
 */
@DpCliComponent
public class DataDirectoryConfiguration extends FileConfiguration {
	/**
	 * The command line parameter (prepended with '--') that is used to configure the location of the data directory to use.
	 */
	public static final String GLOBAL_CONFIGURATION_OPTION_NAME = "dataDirectory";

	/**
	 * The name of the system properties key that is set to the value of the {@link #GLOBAL_CONFIGURATION_OPTION_NAME} configuration option specified
	 * on the command line.
	 */
	public static final String DATA_DIRECTORY_KEY = "data.population.data.directory";

	/**
	 * The offset subtracted from {@link #SET_PROPERTY_ORDER} to be used by {@link #SET_DATA_DIRECTORY_PROPERTY_ORDER}.
	 * See that constant for more information on its usage.
	 */
	protected static final int SET_DATA_DIRECTORY_OFFSET = 30;

	/**
	 * Order value used by {@link #setProperty()}. If the data directory isn't specified we want that to be one of the earliest if not the earliest
	 * validations to fail since it is a core command line option, so we subtract {@link #SET_DATA_DIRECTORY_OFFSET} from {@link #SET_PROPERTY_ORDER}
	 * to ensure it runs ahead of when it normally would.
	 */
	protected static final int SET_DATA_DIRECTORY_PROPERTY_ORDER = SET_PROPERTY_ORDER - SET_DATA_DIRECTORY_OFFSET;

	/**
	 * Constructor setting up this configuration object.
	 */
	public DataDirectoryConfiguration() {
		super(DATA_DIRECTORY_KEY);
		setValueMandatory(true);
		setMustExistOnFileSystem(true);
	}

	/**
	 * Symmetrical method to {@link #setDataDirectory(String)} which delegates its response to {@link #getFileLocation()}.
	 *
	 * @return the data directory location in use.
	 */
	public String getDataDirectory() {
		return getFileLocation();
	}

	/**
	 * Sets the location of the data directory to use. This method is annotated so that it is set if the command line global configuration option
	 * with key {@link #GLOBAL_CONFIGURATION_OPTION_NAME} is specified.
	 *
	 * @param dataDirectoryLocation the data directory location to use.
	 */
	@DpCliOption(key = GLOBAL_CONFIGURATION_OPTION_NAME, hasArgument = true,
			help = "Specifies the location of the data directory to use.")
	public void setDataDirectory(final String dataDirectoryLocation) {
		setFileLocation(dataDirectoryLocation);
	}

	/**
	 * Method overridden to provide a different {@link Order} annotation value than the default one specified by the super class' implementation.
	 * Uses {@link #SET_DATA_DIRECTORY_PROPERTY_ORDER} for its {@link Order} value. See that constant for more information.
	 */
	@Override
	@AfterAllGlobalCliOptions
	@Order(SET_DATA_DIRECTORY_PROPERTY_ORDER)
	public void setProperty() {
		super.setProperty();
	}

	@Override
	protected String getGlobalConfigurationOptionName() {
		return GLOBAL_CONFIGURATION_OPTION_NAME;
	}

	/**
	 * Returns the data directory passed in, since that is the directory this object is configuring.
	 *
	 * @param dataDirectory        the data directory in use by the system.
	 * @param environmentDirectory the environment directory in use by the system.
	 * @return dataDirectory.
	 */
	@Override
	protected String getFileLocation(final String dataDirectory, final String environmentDirectory) {
		return dataDirectory;
	}
}
