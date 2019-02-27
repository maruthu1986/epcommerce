/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.mojo;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;

public class DataPopulationMojoRunnerTest {

	private static final String PATH_TYPE = "dataDirectory";
	public DataPopulationMojoRunner runner;

	@Before
	public void setUp() {
		runner = new DataPopulationMojoRunner();
	}

	@Test(expected = MojoExecutionException.class)
	public void testValidationNullPathForRequiredDirectory() throws MojoExecutionException {
		runner.validatePath(null, PATH_TYPE, true);
	}

	@Test(expected = MojoExecutionException.class)
	public void testValidationNullPathForNonrequiredDirectory() throws MojoExecutionException {
		runner.validatePath(null, PATH_TYPE, false);
	}

	@Test
	public void testValidationRelativePathForRequiredDirectory() throws MojoExecutionException {
		File file = new File("./tmp");
		file.mkdir();
		runner.validatePath(file, PATH_TYPE, true);
		file.delete();
	}

	@Test
	public void testValidationRelativePathForNonrequiredDirectory() throws MojoExecutionException {
		File file = new File("./tmp");
		runner.validatePath(file, PATH_TYPE, false);
	}
}
