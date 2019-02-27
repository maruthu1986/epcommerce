/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.configuration;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.Order;

import com.elasticpath.datapopulation.cli.tool.DataPopulationCliException;
import com.elasticpath.datapopulation.cli.tool.annotations.AfterAllGlobalCliOptions;
import com.elasticpath.datapopulation.core.utils.DpResourceUtils;

/**
 * Helper class to set a System Property pointing to a configured file location.
 * Used by CLI plug-ins for global configuration.
 */
public class FileConfiguration implements ConfiguredDirectoriesAware {
	/**
	 * Set the standard order value for calling {@link #setProperty()} to 100 to allow plenty of methods to be executed either before or afterwards.
	 */
	protected static final int SET_PROPERTY_ORDER = 100;
	private static final Logger LOG = Logger.getLogger(FileConfiguration.class);
	private String systemPropertyKey;
	private String fileLocation;
	private String defaultFilename;
	private boolean valueMandatory;
	private boolean mustExistOnFileSystem;

	/**
	 * Default Constructor.
	 */
	public FileConfiguration() {
		super();
	}

	/**
	 * Constructor which sets the system property key to set.
	 *
	 * @param systemPropertyKey the system property key to set.
	 */
	public FileConfiguration(final String systemPropertyKey) {
		this.systemPropertyKey = systemPropertyKey;
	}

	/**
	 * Sets the system property referenced by {@link #getSystemPropertyKey()} with the value supplied by {@link #getFileLocation()}.
	 * If no value is available and {@link #isValueMandatory()} is true, a {@link DataPopulationCliException} is thrown indicating a missing
	 * mandatory value.
	 */
	@AfterAllGlobalCliOptions
	@Order(SET_PROPERTY_ORDER)
	public void setProperty() {
		final String systemPropertyKey = getSystemPropertyKey();
		String fileLocation = getFileLocation();

		// Now check the file location and if set, set the system property
		if (StringUtils.isNotBlank(systemPropertyKey) && StringUtils.isNotBlank(fileLocation)) {
			setSystemProperty(systemPropertyKey, fileLocation, isMustExistOnFileSystem());
		} else if (isValueMandatory() && StringUtils.isBlank(System.getProperty(systemPropertyKey))) {
			throw new DataPopulationCliException("Error: No value specified for mandatory " + getMissingPropertyValueString());
		}
	}

	/**
	 * Sets the file location of the resource, relative to the directory passed in.
	 * It calls {@link #getDefaultFilename()} to get the filename to use, so if that doesn't return on an {@link IllegalStateException} is thrown,
	 * so that needs to be set before this method is called.
	 *
	 * @param directory the parent directory to use for this resource.
	 */
	public void setParentDirectory(final String directory) {
		final String defaultFilename = getDefaultFilename();

		if (StringUtils.isBlank(defaultFilename)) {
			throw new IllegalStateException("No default filename configured, so cannot set the directory location. "
					+ "Either configure a default filename, or call setFileLocation() instead.");
		}

		setFileLocation(calculateFileLocation(directory, defaultFilename));
	}

	// ConfiguredDirectoriesAware method

	/**
	 * Calls {@link #getFileLocation(String, String)} passing through the data and environment directories passed to this method.
	 * If that method returns a non-blank value then {@link #setFileLocation(String)} is called with that value, otherwise it isn't.
	 *
	 * @param dataDirectoryLocation   the data directory location in use.
	 * @param configDirectoryLocation the environment directory location in use.
	 */
	@Override
	public void directoriesConfigured(final String dataDirectoryLocation, final String configDirectoryLocation) {
		// Only use the packaged directory location if an explicit file location hasn't already been set
		if (StringUtils.isBlank(getFileLocation())) {
			final String fileLocation = getFileLocation(dataDirectoryLocation, configDirectoryLocation);

			if (StringUtils.isNotBlank(fileLocation)) {
				setFileLocation(fileLocation);
			} else {
				LOG.debug("No packaged file path configured, so cannot set the file location from the packaged "
						+ "directory location. Packaged configuration directory provided: " + dataDirectoryLocation
						+ "; config directory provided: " + configDirectoryLocation);
			}
		}
	}

	/**
	 * Returns null by default, but sub-classes can override if they want to be able to be configured from the configured
	 * directories, rather than explicitly.
	 *
	 * @param dataDirectory        the data directory in use by the system.
	 * @param environmentDirectory the environment directory in use by the system.
	 * @return the file location relative to either the data or environment directories, or null if it isn't be configured using them.
	 */
	protected String getFileLocation(final String dataDirectory, final String environmentDirectory) {
		return null;
	}

	// Helper methods

	/**
	 * Sets the system property if the given file location is not blank. Before being set the file location given is converted into a resource
	 * uri by calling {@link DpResourceUtils#getFileResourceUriByDefault(String, boolean)}, this is the value that is set as a System property.
	 * Resource uris are set so that Spring can successfully resolve them.
	 * If mustExistOnFileSystem is true then {@link DpResourceUtils#getFileResourceUriByDefault(String, boolean)} ensures the location refers to the
	 * file system and it also exists otherwise an {@link IllegalArgumentException} is thrown.
	 *
	 * @param systemPropertyKey     the system property key to use, must not be null.
	 * @param fileLocationGiven     the file location to set, if not null.
	 * @param mustExistOnFileSystem true if the location must refer to a file system resource, and already exist; false otherwise.
	 * @see {@link DpResourceUtils#getFileResourceUriByDefault(String, boolean)}
	 */
	protected void setSystemProperty(final String systemPropertyKey, final String fileLocationGiven, final boolean mustExistOnFileSystem) {
		String fileLocation = fileLocationGiven;

		// Only set the system property if we've been given a file location
		if (StringUtils.isNotBlank(fileLocation)) {
			fileLocation = DpResourceUtils.getFileResourceUriByDefault(fileLocation, mustExistOnFileSystem);

			System.setProperty(systemPropertyKey, fileLocation);
		}
	}

	/**
	 * Helper method to return a meaningful description for the type of property this represents; either an external command line argument, or
	 * just simply a system property. This is used to communicate with the end user if there is a problem with the value specified, or if one
	 * hasn't been and it is mandatory.
	 *
	 * @return a description of the type of property this represents, from an end user's perspective, for use in error messages.
	 * @see {@link #getGlobalConfigurationOptionName()}
	 */
	protected String getMissingPropertyValueString() {
		String result;

		final String globalConfigurationOptionName = getGlobalConfigurationOptionName();
		if (StringUtils.isNotBlank(globalConfigurationOptionName)) {
			result = "configuration command line argument --" + globalConfigurationOptionName;
		} else {
			result = "system property " + getSystemPropertyKey();
		}

		return result;
	}

	/**
	 * If this configuration is use to configure a command line argument, sub-classes can override this to provide the name of the configuration
	 * argument. Otherwise null is returned, and it is treated as just configuring a system property.
	 *
	 * @return the name of the associated commmand line argument, or null if it is not exposed via the command line.
	 */
	protected String getGlobalConfigurationOptionName() {
		return null;
	}

	/**
	 * Helper method to calculate the file location string given a base directory location and a filename. This method does not assume the
	 * base directory location is a direct file path, it could also be in url format (such as file: or classpath:) and the method works correctly
	 * with those inputs also.
	 *
	 * @param parentDirectoryLocation the parent directory location to use, must not be null.
	 * @param filename                the filename to use, must not be null.
	 * @return the file location for the filename provided, relative to the parent directory provided.
	 */
	protected String calculateFileLocation(final String parentDirectoryLocation, final String filename) {
		final StringBuilder fileLocation = new StringBuilder();

		// Don't assume the base directory is a file system path, it may specify a spring resource loader scheme (such as classpath:)
		// Currently only a file system path is supported by other actors in the system (e.g. Import/Export) but this change
		// So we don't assume we're given a file system path.

		fileLocation.append(parentDirectoryLocation);

		if (!parentDirectoryLocation.endsWith("/") && !parentDirectoryLocation.endsWith("\\")) {
			fileLocation.append(File.separatorChar);
		}

		fileLocation.append(filename);

		return fileLocation.toString();
	}

	// Getters and Setters

	public String getSystemPropertyKey() {
		return this.systemPropertyKey;
	}

	public void setSystemPropertyKey(final String systemPropertyKey) {
		this.systemPropertyKey = systemPropertyKey;
	}

	public String getFileLocation() {
		return this.fileLocation;
	}

	public void setFileLocation(final String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getDefaultFilename() {
		return this.defaultFilename;
	}

	public void setDefaultFilename(final String defaultFilename) {
		this.defaultFilename = defaultFilename;
	}

	public boolean isValueMandatory() {
		return this.valueMandatory;
	}

	public void setValueMandatory(final boolean valueMandatory) {
		this.valueMandatory = valueMandatory;
	}

	public boolean isMustExistOnFileSystem() {
		return this.mustExistOnFileSystem;
	}

	public void setMustExistOnFileSystem(final boolean mustExistOnFileSystem) {
		this.mustExistOnFileSystem = mustExistOnFileSystem;
	}
}
