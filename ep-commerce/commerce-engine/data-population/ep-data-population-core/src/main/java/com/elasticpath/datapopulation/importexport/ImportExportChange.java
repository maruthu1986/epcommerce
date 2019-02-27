/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.importexport;

import java.io.File;
import java.io.IOException;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.importexport.client.Index;

/**
 * A custom Liquibase change class that invokes Import/Export on a dataset defined in this class' configured properties.
 */
public class ImportExportChange implements CustomTaskChange {
	/**
	 * The System Property key pointing to the common data directory in use which contains the datasets to process.
	 *
	 * @see {@link #getDataDirectory()} for more information as well as {@link #calculateImportExportConfigurationDirectoryPath()}.
	 */
	public static final String DATA_DIRECTORY_PROPERTY = "data.population.data.directory";
	/**
	 * The default name of the Import/Export directory stored under the dataset directory.
	 *
	 * @see {@link #getImportExportDirectory()}.
	 */
	public static final String DEFAULT_IMPORT_EXPORT_DIRECTORY = "importexport";
	/**
	 * The name of the Import/Export configuration directory expected under the main Import/Export directory.
	 * Currently this name is not configurable.
	 */
	public static final String IMPORT_EXPORT_CONFIGURATION_DIRECTORY = "configuration";
	/**
	 * The name of the Import/Export import configuration filename expected under the Import/Export configuration directory.
	 * Currently this name is not configurable.
	 */
	public static final String IMPORT_CONFIGURATION_FILE = "importconfiguration.xml";
	private static final Logger LOG = Logger.getLogger(ImportExportChange.class);
	private String dataSet;
	private String dataDirectory;
	private String importExportDirectory;

	private String importExportConfigurationDirectoryPath;

	@Override
	public void setUp() throws SetupException {
		// Not implemented.
	}

	/**
	 * Uses the configured information to invoke the Import/Export tool to import the data selected.
	 *
	 * @param database the database object provided by Liquibase.
	 * @throws CustomChangeException if there was an error processing the request, either by this class or by Import/Export.
	 */
	@Override
	public void execute(final Database database) throws CustomChangeException {
		final Index importExportApp = new Index();

		try {
			final String importExportConfigDir = getImportExportConfigurationDirectoryPath();

			LOG.info("Attempting to run Elastic Path's Import/Export tool on configuration directory: " + importExportConfigDir);
			validateDirectory(importExportConfigDir, "Import/Export configuration directory");

			final int exitCode = importExportApp.doImport(importExportConfigDir + File.separator + IMPORT_CONFIGURATION_FILE);

			// The exit code returned indicates if the import was successful or not, so we need to check it
			if (exitCode != 0) {
				// Include the dataset name in the error message if specified - it may not be if the config dir was explicitly set
				final String dataset = getDataSet();

				if (StringUtils.isNotEmpty(dataset)) {
					throw new CustomChangeException(
							String.format("A problem occurred importing dataset '%s', "
											+ "please see logs for more information. Import/Export Configuration directory: %s",
									dataset, importExportConfigDir));
				} else {
					throw new CustomChangeException(
							"A problem occurred importing dataset, please see logs for more information. Import/Export Configuration directory: "
									+ importExportConfigDir);
				}
			}
		} catch (final IOException e) {
			throw new CustomChangeException(e);
		}
	}

	@Override
	public String getConfirmationMessage() {
		return String.format("Import/Export Changeset was successfully applied: '%s' (sub-directory: %s)",
				getDataSet(), getImportExportDirectory());
	}

	@Override
	public ValidationErrors validate(final Database database) {
		return null;
	}

	@Override
	public void setFileOpener(final ResourceAccessor resourceAccessor) {
		// Not implemented.
	}

	/**
	 * Calculates the file path of the Import/Export configuration directory to use.
	 * This returns a directory path as follows:
	 * <p>
	 * {@link #getDataDirectory()}/{@link #getDataSet()}/{@link #getImportExportDirectory()}
	 *
	 * @return the calculated file path of the Import/Export configuration directory to use.
	 * @throws IOException if there was an error calculating the data directory for example if one of the directories in the path does not exist.
	 */
	protected String calculateImportExportConfigurationDirectoryPath() throws IOException {
		final String dataDirectory = getDataDirectory();
		final String dataSet = getDataSet();
		final String importExportDirectory = getImportExportDirectory();

		validateDirectory(dataDirectory, "Top level data directory");
		validateDirectory(dataDirectory + File.separator + dataSet, "Dataset directory");
		validateDirectory(dataDirectory + File.separator + dataSet + File.separator + importExportDirectory, "Import/Export data directory");

		return dataDirectory + File.separator + dataSet
				+ File.separator + importExportDirectory + File.separator + IMPORT_EXPORT_CONFIGURATION_DIRECTORY;
	}

	/**
	 * Checks that the directory path passed in both exists and is a directory. If not an {@link IOException} is thrown.
	 *
	 * @param directoryPath        the directory path to verify.
	 * @param directoryDescription a description of what the directory path is used for to be used in any {@link IOException} message thrown.
	 * @throws IOException if the directory path passed in does not exist or is not a directory.
	 */
	protected void validateDirectory(final String directoryPath, final String directoryDescription) throws IOException {
		if (StringUtils.isBlank(directoryPath)) {
			throw new IllegalStateException("No " + directoryDescription + " has been configured; please configure it before retrying.");
		}

		final File directory = new File(directoryPath);

		if (!directory.exists()) {
			throw new IOException(directoryDescription + " '" + directoryPath + "' does not exist.");
		}

		if (!directory.isDirectory()) {
			throw new IOException(directoryDescription + " '" + directoryPath + "' is not a directory.");
		}
	}

	// Getters and Setters

	/**
	 * Returns the overall data directory in use for database updates. If not set explicitly, it falls back to looking up the value stored under
	 * system property {@link #DATA_DIRECTORY_PROPERTY}. Using a system property allows all instances of this class to share a common data directory
	 * without having to set it individually in Liquibase changesets. Sharing a common data directory for a single invocation of Liquibase is common.
	 *
	 * @return the overall data directory in use for database updates.
	 */
	public String getDataDirectory() {
		// Use a specified data directory, falling back if not set explicitly
		if (this.dataDirectory == null) {
			// Otherwise check if the location has been specified by system property
			// This is done to avoid having to set the value on every single instance of this class as it is normally constant across all of them
			this.dataDirectory = System.getProperty(DATA_DIRECTORY_PROPERTY);
		}

		return this.dataDirectory;
	}

	/**
	 * Sets the overall data directory in use for database updates. Setting this to a non-null value means {@link #getDataDirectory()} does not
	 * fall back to looking up the value stored under a system property (@link #DATA_DIRECTORY_PROPERTY}, otherwise it does.
	 *
	 * @param dataDirectory the overall data directory in use.
	 */
	public void setDataDirectory(final String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}

	/**
	 * Returns the name of the dataset being processed. If specified, this should be a directory that exists under the directory returned by
	 * {@link #getDataDirectory()}. If may be null if a full directory path to the Import/Export configuration directory has been set via
	 * {@link #setImportExportConfigurationDirectoryPath(String)}.
	 *
	 * @return the name of the dataset to process, may be null.
	 */
	public String getDataSet() {
		return this.dataSet;
	}

	/**
	 * Sets the name of the dataset being processed, may be null if the full directory path to the Import/Export configuration directory has
	 * been set via {@link #setImportExportConfigurationDirectoryPath(String)}.
	 *
	 * @param dataSet the name of the dataset.
	 */
	public void setDataSet(final String dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * Returns the name of the Import/Export directory that contains the configuration directory containing the import configuration file.
	 * These should respectively be named {@link #IMPORT_EXPORT_CONFIGURATION_DIRECTORY} and {@link #IMPORT_CONFIGURATION_FILE}.
	 * If this property has not been set via {@link #setImportExportDirectory(String)} then the default Import/Export directory name is returned:
	 * {@link #DEFAULT_IMPORT_EXPORT_DIRECTORY}.
	 *
	 * @return the name of the Import/Export directory set, or {@link #DEFAULT_IMPORT_EXPORT_DIRECTORY} if not set.
	 */
	public String getImportExportDirectory() {
		if (this.importExportDirectory == null) {
			this.importExportDirectory = DEFAULT_IMPORT_EXPORT_DIRECTORY;
		}
		return this.importExportDirectory;
	}

	/**
	 * Sets the name of the Import/Export directory, see {@link #getImportExportDirectory()} for more information on what this directory is.
	 *
	 * @param importExportDirectory the name of the Import/Export directory.
	 */
	public void setImportExportDirectory(final String importExportDirectory) {
		this.importExportDirectory = importExportDirectory;
	}

	/**
	 * Returns the path to the Import/Export configuration directory to process. If not set explicitly through
	 * {@link #setImportExportConfigurationDirectoryPath(String)}, then {@link #calculateImportExportConfigurationDirectoryPath()} is called
	 * to calculate this based on the values returned by {@link #getDataDirectory()}, {@link #getDataSet()} and {@link #getImportExportDirectory()}.
	 * Therefore if not explicitly set, these intermediary values must be set instead.
	 *
	 * @return the path to the Import/Export configuration directory to process.
	 * @throws IOException if the Import/Export configuration directory was not explicitly set by
	 *                     {@link #setImportExportConfigurationDirectoryPath(String)} and there was a problem calculating it.
	 *                     See {@link #calculateImportExportConfigurationDirectoryPath()} for more information.
	 */
	public String getImportExportConfigurationDirectoryPath() throws IOException {
		if (this.importExportConfigurationDirectoryPath == null) {
			this.importExportConfigurationDirectoryPath = calculateImportExportConfigurationDirectoryPath();
		}
		return this.importExportConfigurationDirectoryPath;
	}

	/**
	 * Sets the path to the Import/Export configuration directory to process, may be null. If null the value is calculated, see
	 * {@link #getImportExportConfigurationDirectoryPath()} for more details.
	 *
	 * @param importExportConfigurationDirectoryPath the path to the Import/Export configuration directory to process.
	 */
	public void setImportExportConfigurationDirectoryPath(final String importExportConfigurationDirectoryPath) {
		this.importExportConfigurationDirectoryPath = importExportConfigurationDirectoryPath;
	}
}