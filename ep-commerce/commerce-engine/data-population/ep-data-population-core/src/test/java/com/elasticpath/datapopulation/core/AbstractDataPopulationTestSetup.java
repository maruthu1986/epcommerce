/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core;

import java.io.File;

import org.junit.Before;
import org.springframework.context.ApplicationContext;

/**
 * Super class that sets up the default test environment. Changes to environmentDirectory/ folder will affect the test results.
 */
public abstract class AbstractDataPopulationTestSetup { //NOPMD

	private File workingDirectory;
	private File outputDirectory;
	private DataPopulationCore core;

	/**
	 * Sets up the data population application context and data population core.
	 */
	@Before
	public void setUp() {
		setWorkingDirectory(new File("tmp"));
		setOutputDirectory(new File("output"));
		ApplicationContext context = DataPopulationContextInitializer.initializeContext(
				this.getClass().getClassLoader().getResource("dataDirectory").getPath(),
				this.getClass().getClassLoader().getResource("configDirectory").getPath(),
				getWorkingDirectory().getAbsolutePath(),
				"/test-context.xml"
		);

		setCore((DataPopulationCore) context.getBean("dataPopulationCore"));
	}

	protected File getWorkingDirectory() {
		return workingDirectory;
	}

	protected void setWorkingDirectory(final File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	protected File getOutputDirectory() {
		return outputDirectory;
	}

	protected void setOutputDirectory(final File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	protected DataPopulationCore getCore() {
		return core;
	}

	protected void setCore(final DataPopulationCore core) {
		this.core = core;
	}
}
