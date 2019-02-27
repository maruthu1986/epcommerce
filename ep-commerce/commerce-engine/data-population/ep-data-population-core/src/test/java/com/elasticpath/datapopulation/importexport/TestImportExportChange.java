/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.importexport;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * test case for Import Export change.
 */
public class TestImportExportChange {

	private ImportExportChange importExportChange;

	/**
	 * test case setup.
	 */
	@Before
	public void setUp() {
		importExportChange = new ImportExportChange();
	}

	/**
	 * Test calculate Import Export configuration path.
	 *
	 * @throws IOException if directory is not exist.
	 */
	@Test
	public void testCalculateImportExportConfigurationDirectoryPath() throws IOException {
		importExportChange = new ImportExportChange() {
			public void validateDirectory(final String dir, final String dirDesc) {
				//empty method.
			}
		};

		importExportChange.setDataDirectory("test1");
		importExportChange.setDataSet("test2");

		String result = importExportChange.calculateImportExportConfigurationDirectoryPath();

		String expectedResult = "test1" + File.separator + "test2" + File.separator
				+ ImportExportChange.DEFAULT_IMPORT_EXPORT_DIRECTORY + File.separator
				+ ImportExportChange.IMPORT_EXPORT_CONFIGURATION_DIRECTORY;

		assertThat(result)
				.isEqualTo(expectedResult);
	}

	/**
	 * test getImportExportDirectory.
	 */
	@Test
	public void testGetImportExportDirectory() {
		importExportChange.setImportExportDirectory(null);
		String result = importExportChange.getImportExportDirectory();

		assertThat(result)
				.isEqualTo(ImportExportChange.DEFAULT_IMPORT_EXPORT_DIRECTORY);

		importExportChange.setImportExportDirectory("test");
		result = importExportChange.getImportExportDirectory();

		assertThat(result)
				.isEqualTo("test");

	}

}
