/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.shell.Bootstrap;

/**
 * This is the CLI program to invoke the EP's Data Population CLI Program.
 */
public class DataPopulationCliApplication {
	/**
	 * The return code to exit the system with from the {@link #main(String[])} method if a {@link DataPopulationCliException} has been thrown.
	 */
	public static final int DATA_POPULATION_EXCEPTION_RETURN_CODE = 1;
	private static final Logger LOG = Logger.getLogger(DataPopulationCliApplication.class);
	private DataPopulationCliApplicationInitializer initializer;
	private DataPopulationCliGlobalConfigurer globalConfiguration;

	// Delegate methods

	/**
	 * Application's command-line entry point.
	 *
	 * @param args the command line arguments to process
	 * @throws IOException if Spring-Shell throws an exception processing the request.
	 */
	@SuppressWarnings("PMD.DoNotCallSystemExit")
	public static void main(final String[] args) {
		try {
			// Create the program and initialize it, this starts the program's initial ApplicationContext among other things
			final DataPopulationCliApplication program = new DataPopulationCliApplication();
			program.initializeApplication();

			// Next process the command line args to process any global configuration arguments
			// Note: This is an extension to what Spring Shell currently supports so we pre-process
			// those arguments before passing the remaining command arguments (if any) through to Spring Shell below

			final String[] remainingCommandArgs;
			try {
				remainingCommandArgs = program.processGlobalConfiguration(args);
			} finally {
				program.closeApplicationContext();
			}

			// Finally pass control over to Spring Shell now we've configured our environment
			// Note this creates a brand new Spring context, and so any environment configuration
			// should be done through System Properties
			program.invokeSpringShell(remainingCommandArgs);
		} catch (final DataPopulationCliException | IOException e) {
			LOG.error("Error: A DataPopulationCliException occurred processing request. Message: " + e.getMessage(), e);
			System.exit(DATA_POPULATION_EXCEPTION_RETURN_CODE);
		}
	}

	/**
	 * Initializes the application by delegating to the {@link DataPopulationCliApplicationInitializer} and calling its
	 * {@link DataPopulationCliApplicationInitializer#initializeApplication()} method.
	 */
	public void initializeApplication() {
		getInitializer().initializeApplication();
	}

	/**
	 * Gets the Spring application context in use by delegating to the the {@link DataPopulationCliApplicationInitializer} and calling its
	 * {@link DataPopulationCliApplicationInitializer#getApplicationContext()} method.
	 *
	 * @return the Spring application context in use.
	 */
	public GenericApplicationContext getApplicationContext() {
		return getInitializer().getApplicationContext();
	}

	/**
	 * Closes the Spring application context in use by delegating to the the {@link DataPopulationCliApplicationInitializer} and calling its
	 * {@link DataPopulationCliApplicationInitializer#closeApplicationContext()} method.
	 */
	public void closeApplicationContext() {
		getInitializer().closeApplicationContext();
	}

	// Spring-Shell methods

	/**
	 * Processes the global configuration arguments specified in the command line arguments passed in, returning the remaining command line arguments
	 * not processed, from the first command line argument not part of the configured global configuration options. Delegates to the
	 * {@link DataPopulationCliGlobalConfigurer} for processing by calling its
	 * {@link DataPopulationCliGlobalConfigurer#processGlobalConfiguration(String[])} method.
	 *
	 * @param commandLineArgs the command line arguments to parse.
	 * @return the remaining unparsed command line arguments that do not make up valid global configuration arguments, starting from the first
	 * unrecognized command line argument.
	 */
	public String[] processGlobalConfiguration(final String[] commandLineArgs) {
		return getGlobalConfiguration().processGlobalConfiguration(commandLineArgs);
	}

	// Factory methods

	/**
	 * Entry method for invoking Spring-Shell with the arguments given. This method should be invoked using the command line arguments returned from
	 * calling {@link #processGlobalConfiguration(String[])} first.
	 *
	 * @param args the command line arguments that Spring-Shell should process.
	 * @throws IOException if Spring-Shell encounters an issue processing the request.
	 */
	protected void invokeSpringShell(final String[] args) throws IOException {
		Bootstrap.main(args);
	}

	/**
	 * Factory method to create the {@link DataPopulationCliApplicationInitializer} to use if one isn't provided explicitly by calling
	 * {@link #setInitializer(DataPopulationCliApplicationInitializer)}.
	 *
	 * @return a {@link DataPopulationCliApplicationInitializer} instance to use if one isn't provided explicitly
	 */
	protected DataPopulationCliApplicationInitializer createProgramInitializer() {
		return new DataPopulationCliApplicationInitializer();
	}

	/**
	 * Factory method to create the {@link DataPopulationCliGlobalConfigurer} to use if one isn't provided explicitly by calling
	 * {@link #setGlobalConfiguration(DataPopulationCliGlobalConfigurer)}.
	 * Note: This method cannot be called before {@link #initializeApplication()} since it uses the application context available after initializing
	 * the application.
	 *
	 * @return a {@link DataPopulationCliGlobalConfigurer} instance to use if one isn't provided explicitly
	 */
	protected DataPopulationCliGlobalConfigurer createGlobalConfiguration() {
		final DataPopulationCliApplicationInitializer initializer = getInitializer();

		if (!initializer.isInitialized()) {
			throw new IllegalStateException("Unable to create global configuration object until the program has been initialized. "
					+ "See DataPopulationCliApplication.initializeApplication()");
		}

		return createGlobalConfiguration(initializer.getApplicationContext());
	}

	// Getters and Setters

	/**
	 * Factory method to create a {@link DataPopulationCliGlobalConfigurer} object with the given application context.
	 *
	 * @param beanFactory the application context to create the configurer for.
	 * @return a {@link DataPopulationCliGlobalConfigurer} object with the given application context.
	 */
	protected DataPopulationCliGlobalConfigurer createGlobalConfiguration(final ListableBeanFactory beanFactory) {
		return new DataPopulationCliGlobalConfigurer(beanFactory);
	}

	/**
	 * Returns the {@link DataPopulationCliApplicationInitializer} in use by this object. If no object is currently configured it calls
	 * {@link #createProgramInitializer()} and sets that as the one in use, before returning it.
	 *
	 * @return the initializer currently in use by this object, never null.
	 */
	protected DataPopulationCliApplicationInitializer getInitializer() {
		if (this.initializer == null) {
			this.initializer = createProgramInitializer();
		}
		return this.initializer;
	}

	/**
	 * Sets the {@link DataPopulationCliApplicationInitializer} to use by this object.
	 *
	 * @param initializer the {@link DataPopulationCliApplicationInitializer} to use by this object.
	 */
	public void setInitializer(final DataPopulationCliApplicationInitializer initializer) {
		this.initializer = initializer;
	}

	/**
	 * Returns the {@link DataPopulationCliGlobalConfigurer} in use by this object. If no object is currently configured it calls
	 * {@link #createGlobalConfiguration()} and sets that as the one in use, before returning it.
	 *
	 * @return the {@link DataPopulationCliGlobalConfigurer} in use by this object, never null.
	 */
	protected DataPopulationCliGlobalConfigurer getGlobalConfiguration() {
		if (this.globalConfiguration == null) {
			this.globalConfiguration = createGlobalConfiguration();
		}
		return this.globalConfiguration;
	}

	// Main method

	/**
	 * Sets the {@link DataPopulationCliGlobalConfigurer} to use by this object.
	 *
	 * @param globalConfiguration the {@link DataPopulationCliGlobalConfigurer} to use by this object.
	 */
	public void setGlobalConfiguration(final DataPopulationCliGlobalConfigurer globalConfiguration) {
		this.globalConfiguration = globalConfiguration;
	}
}
