/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.elasticpath.datapopulation.core.utils.DpUtils;

/**
 * An initializer class which is used by {@link DataPopulationCliApplication} to initialize the application by checking environment variables,
 * setting appropriate system properties and initializing the application context used to process global configuration command line arguments.
 */
public class DataPopulationCliApplicationInitializer {
	/**
	 * The path to the overall Spring context file for the global configuration processing.
	 */
	public static final String GLOBAL_CONFIGURATION_CONTEXT = "classpath:/META-INF/spring/dpcli-global-configuration.xml";
	/**
	 * System property key that is set from the environment value configured for environment variable
	 * {@link #DATA_POPULATION_HOME_ENVIRONMENT_VARIABLE}.
	 */
	public static final String DATA_POPULATION_HOME_KEY = "data.population.home.directory";
	/**
	 * System property key that is set from the environment value configured for environment variable
	 * {@link #DATA_POPULATION_WORKING_DIRECTORY_VARIABLE}, or if not set the default working directory see
	 * {@link #DATA_POPULATION_WORKING_DIRECTORY_VARIABLE} for more information.
	 */
	public static final String DATA_POPULATION_WORKING_DIRECTORY_KEY = "data.population.working.directory";
	/**
	 * System property key that is set from the environment value configured for environment variable
	 * {@link #DYNAMIC_CONFIG_CP_ENVIRONMENT_VARIABLE}.
	 */
	public static final String DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY_KEY = "data.population.dynamic.configuration.classpath.directory";
	/**
	 * Mandatory environment variable used to point to the directory where the Data Population CLI application is running from.
	 */
	protected static final String DATA_POPULATION_HOME_ENVIRONMENT_VARIABLE = "DATA_POPULATION_HOME";
	/**
	 * Optional environment variable used to point to the temporary working directory that the Data Population CLI application can use (and delete)
	 * during this lifecycle of the application. If not specified it defaults to using the {@link #DEFAULT_WORKING_DIRECTORY_SUBPATH} directory name
	 * inside the DATA_POPULATION_HOME directory.
	 */
	protected static final String DATA_POPULATION_WORKING_DIRECTORY_VARIABLE = "DATA_POPULATION_WORKING_DIRECTORY";
	/**
	 * Mandatory environment variable used to point to the directory the 'home' directory of the current JVM.
	 */
	protected static final String JAVA_HOME_ENVIRONMENT_VARIABLE = "JAVA_HOME";
	/**
	 * Mandatory environment variable used to point to a directory on the classpath that can contain dynamically generated classpath files such as
	 * importexporttool.config which is required to be on the classpath by the Import/Export tool (up to version 6.9).
	 */
	protected static final String DYNAMIC_CONFIG_CP_ENVIRONMENT_VARIABLE = "DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY";
	/**
	 * Default working directory to use (relative to the DATA_POPULATION_HOME directory) if one isn't explicitly set by configuring the
	 * {@link #DATA_POPULATION_WORKING_DIRECTORY_VARIABLE} environment variable.
	 */
	protected static final String DEFAULT_WORKING_DIRECTORY_SUBPATH = "tmp";
	private static final Logger LOG = Logger.getLogger(DataPopulationCliApplicationInitializer.class);
	private GenericApplicationContext applicationContext;
	private boolean initialized;

	/**
	 * Initializes the program by checking or mandatory environment variables such as DATA_POPULATION_HOME, as well as setting up system properties
	 * required by this application, and setting its working directory.
	 *
	 * @see {@link #checkEnvironment()}
	 * @see {@link #setSystemProperties()}
	 * @see {@link #setWorkingDirectory()}
	 */
	public void initializeApplication() {
		if (isInitialized()) {
			LOG.warn("initializeApplication() already called, so ignoring duplicate initialization request.");
		} else {
			// Make sure the environment this application is running is valid to minimize odd environmental, hard to diagnose, errors later on.
			checkEnvironment();
			// We use the configuration from the shell scripts that invoked us and set the system properties required by this program
			setSystemProperties();
			// Now set the working directory for use by the application. It uses the system properties set in the above call
			setWorkingDirectory();

			// Finally we can initialize the application context
			initializeApplicationContext();

			setInitialized(true);
		}
	}

	// Validation and Setup methods

	/**
	 * Checks the environment variables to make sure the mandatory ones are set. Note, the environment variables that are used to set
	 * system properties are checked by {@link #setSystemProperties()}.
	 */
	protected void checkEnvironment() {
		// The application requires JAVA_HOME set to allow plugins such as the ant plugin to perform correctly
		checkEnvironmentVariableIsSet(JAVA_HOME_ENVIRONMENT_VARIABLE);
	}

	/**
	 * Sets the system properties in use by the system from the values specified as environment variables.
	 */
	protected void setSystemProperties() {
		setMandatoryPropertyFromEnvironment(DATA_POPULATION_HOME_ENVIRONMENT_VARIABLE, DATA_POPULATION_HOME_KEY);
		setMandatoryPropertyFromEnvironment(DYNAMIC_CONFIG_CP_ENVIRONMENT_VARIABLE,
				DYNAMIC_CONFIGURATION_CLASSPATH_DIRECTORY_KEY);
	}

	/**
	 * Sets the working directory system property as well as clearing out the working directory, or creating it, ready for use.
	 */
	protected void setWorkingDirectory() {
		final String workingDirectoryEnvironmentValue = System.getenv(DATA_POPULATION_WORKING_DIRECTORY_VARIABLE);
		final File workingDirectory;

		if (StringUtils.isNotBlank(workingDirectoryEnvironmentValue)) {
			workingDirectory = new File(workingDirectoryEnvironmentValue);
		} else {
			workingDirectory = new File(System.getProperty(DATA_POPULATION_HOME_KEY), DEFAULT_WORKING_DIRECTORY_SUBPATH);
		}

		LOG.debug("Using working directory: " + workingDirectory.getAbsolutePath());

		// Delete it if it already exists to clear out any existing content
		if (workingDirectory.exists()) {
			try {
				FileUtils.deleteDirectory(workingDirectory);
			} catch (final IOException e) {
				throw new DataPopulationCliException("Error: Unable to delete existing Data Population working directory: "
						+ workingDirectory.getAbsolutePath() + DpUtils.getNestedExceptionMessage(e), e);
			}
		}

		// Create it again so it's available for use
		if (!workingDirectory.mkdirs()) {
			throw new DataPopulationCliException("Error: Unable to create Data Population working directory: "
					+ workingDirectory.getAbsolutePath());
		}

		System.setProperty(DATA_POPULATION_WORKING_DIRECTORY_KEY, workingDirectory.getAbsolutePath());
	}

	/**
	 * Helper method check if a particular environment variable is set. If not a {@link DataPopulationCliException} is thrown indicating the
	 * mandatory environment variable has not been set.
	 *
	 * @param environmentVariable the environment variable to check.
	 * @throws DataPopulationCliException is thrown indicating the mandatory environment variable has not been set.
	 */
	protected void checkEnvironmentVariableIsSet(final String environmentVariable) throws DataPopulationCliException {
		if (StringUtils.isBlank(System.getenv(environmentVariable))) {
			throw new DataPopulationCliException("Error: No '" + environmentVariable + "' environment variable defined. "
					+ "This is required in order for this application to function correctly.");
		}
	}

	/**
	 * Sets a system property with the given system property key from the environment value configured for the given environment. If no environment
	 * value has been specified a {@link DataPopulationCliException} is thrown instead.
	 *
	 * @param environmentVariable the environment variable to read.
	 * @param systemPropertyKey   the system property to set
	 * @throws DataPopulationCliException if no environment value has been set for the environment variable passed in.
	 */
	protected void setMandatoryPropertyFromEnvironment(final String environmentVariable, final String systemPropertyKey)
			throws DataPopulationCliException {
		final String environmentVariableValue = System.getenv(environmentVariable);

		if (StringUtils.isBlank(environmentVariableValue)) {
			throw new DataPopulationCliException("No '" + environmentVariable + "' environment variable specified. This value is required, "
					+ "and should be supplied by the data-population bootstrap script.");
		}

		System.setProperty(systemPropertyKey, environmentVariableValue);
	}

	// Spring Application Context methods

	/**
	 * Initializes the application context by calling {@link #createApplicationContext()} and setting it as the one in use.
	 * If {@link #getApplicationContext()} doesn't return null before that an {@link IllegalArgumentException} is thrown.
	 *
	 * @throws IllegalArgumentException if an application context is already active (i.e. {@link #getApplicationContext()} returns a non-null value).
	 */
	public void initializeApplicationContext() {
		if (getApplicationContext() != null) {
			throw new IllegalStateException("Already initialized application context.");
		}
		setApplicationContext(createApplicationContext());
	}

	/**
	 * Closes the application context in use, and sets it to null. If the application context is already null, this method does nothing.
	 */
	public void closeApplicationContext() {
		final GenericApplicationContext applicationContext = getApplicationContext();

		if (applicationContext != null) {
			applicationContext.close();
			setApplicationContext(null);
		}
	}

	// Factory methods

	/**
	 * Creates the Spring application context to use to process the global configuration command line arguments.
	 * This method creates it from reading in the file pointed to by {@link #GLOBAL_CONFIGURATION_CONTEXT}.
	 *
	 * @return the newly created Spring application context to use.
	 */
	protected GenericApplicationContext createApplicationContext() {
		final GenericApplicationContext result = new GenericXmlApplicationContext(GLOBAL_CONFIGURATION_CONTEXT);
		result.registerShutdownHook();
		return result;
	}

	// Getters and Setters

	public GenericApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	public void setApplicationContext(final GenericApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	protected void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}
}