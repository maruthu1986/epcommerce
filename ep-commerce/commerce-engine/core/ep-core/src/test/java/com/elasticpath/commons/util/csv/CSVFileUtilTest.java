/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.commons.util.csv;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.commons.io.FileUtils;

/**
 * Test for getting remote CSV file name.
 */
public class CSVFileUtilTest {

	private File importAssetFolder;

	@Before
	public void init() {
		importAssetFolder = getImportAssetFolder();
	}

	@After
	public void tearDown() {
		FileUtils.deleteQuietly(importAssetFolder);
	}


	@Test
	public void shouldReturnFullCSVFilePathWithoutSubFolder() throws Exception {
		File csvFile = createDeletableTempFile(importAssetFolder);

		String expectedCSVFilePath = csvFile.getAbsolutePath();
		String actualCSVFilePath = CSVFileUtil.getRemoteCsvFileName(importAssetFolder.getAbsolutePath(), csvFile.getName());

		assertEquals(expectedCSVFilePath, actualCSVFilePath);
	}

	@Test
	public void shouldReturnFullCSVFilePathWithSubFolder() throws Exception {
		File subfolder = new File(importAssetFolder, "subfolder");
		subfolder.mkdirs();
		subfolder.deleteOnExit();

		File csvFile = createDeletableTempFile(subfolder);

		String expectedCSVFilePath = csvFile.getAbsolutePath();
		String actualCSVFilePath = CSVFileUtil.getRemoteCsvFileName(subfolder.getAbsolutePath(), csvFile.getName());

		assertEquals(expectedCSVFilePath, actualCSVFilePath);
	}

	private File createDeletableTempFile(final File importAssetDir) throws Exception {
		File deletableTempFile = File.createTempFile("test_", ".csv", importAssetDir);
		deletableTempFile.deleteOnExit();

		return deletableTempFile;
	}

	private File getImportAssetFolder() {
		File importAssetFolder = new File(getClass().getClassLoader().getResource(".").getPath(), "csvimport");
		importAssetFolder.mkdirs();

		return importAssetFolder;
	}
}
