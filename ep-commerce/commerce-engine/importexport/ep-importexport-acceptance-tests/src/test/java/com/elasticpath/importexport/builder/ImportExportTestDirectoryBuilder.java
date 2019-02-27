/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.builder;

import java.io.File;

/**
 * Builds a {@link java.io.File} for importexport test directory.
 */
public class ImportExportTestDirectoryBuilder {

	private String baseDirectoryName = "target/importexport";

	private String testName;

	private int runNumber;

	/**
	 * Creates a new instance of the builder.
	 *
	 * @return the import export test directory builder
	 */
	public static ImportExportTestDirectoryBuilder newInstance() {
		return new ImportExportTestDirectoryBuilder();
	}

	/**
	 * Builds the file object.
	 *
	 * @return the file object
	 */
	public File build() {
		File testDirectory = new File(baseDirectoryName);
		testDirectory = new File(testDirectory, testName);
		testDirectory = new File(testDirectory, String.valueOf(runNumber));
		return testDirectory;
	}

	/**
	 * With base directory name.
	 *
	 * @param baseDirectoryName the base directory name
	 * @return the import export test directory builder
	 */
	public ImportExportTestDirectoryBuilder withBaseDirectoryName(final String baseDirectoryName) {
		this.baseDirectoryName = baseDirectoryName;
		return this;
	}

	/**
	 * With test name.
	 *
	 * @param testName the test name
	 * @return the import export test directory builder
	 */
	public ImportExportTestDirectoryBuilder withTestName(final String testName) {
		this.testName = testName;
		return this;
	}

	/**
	 * With run number.
	 *
	 * @param runNumber the run number
	 * @return the import export test directory builder
	 */
	public ImportExportTestDirectoryBuilder withRunNumber(final int runNumber) {
		this.runNumber = runNumber;
		return this;
	}

}
