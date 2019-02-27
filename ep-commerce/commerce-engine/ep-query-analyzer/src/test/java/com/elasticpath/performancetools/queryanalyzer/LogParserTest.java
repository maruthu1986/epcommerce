/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.performancetools.queryanalyzer.beans.QueryStatistics;
import com.elasticpath.performancetools.queryanalyzer.utils.SystemProperties;

/**
 * Test class for {@link LogParser}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogParserTest {

	@After
	public void clean() {
		System.getProperties().remove(SystemProperties.OUTPUT_JSON_FILE_PATH_SYSTEM_PROPERTY);
	}

	@Test
	public void shouldParseLogFileAndCreateStatisticsInstance() throws Exception {
		final File testCortexLog = new File(getResourcePathFromClassLoader("TestCortex.log"));
		final QueryStatistics expectedStatistics = restoreQueryStatisticsFromFile("partial_statistics.ser");

		final QueryStatistics actualStatistics = LogParser.INSTANCE.parse(testCortexLog);
		/*
			If any of objects, constituents of QueryStatistics, is changed then partial_statistics.ser must be recreated with
			The generated file will be saved under this project base folder.

			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("partial_statistics.ser"));
			os.writeObject(actualStatistics);
		 */

		//after parsing, QueryStatistics contains only operations
		assertThat(actualStatistics.getOperations())
				.as("Expected and actual operations must be the same")
				.isEqualTo(expectedStatistics.getOperations());

	}

	@Test
	public void shouldGenerateStatisticsAndSaveToJSON() throws Exception {
		//where to store generated statistics
		final String outputJSONFilePath = getResourcePathFromClassLoader(".") + "db_statistics.json";

		System.setProperty(SystemProperties.OUTPUT_JSON_FILE_PATH_SYSTEM_PROPERTY, outputJSONFilePath);
		final QueryStatistics actualStatistics = restoreQueryStatisticsFromFile("partial_statistics.ser");

		LogParser.INSTANCE.generateStatistics(actualStatistics);

		/*
			If any of objects, constituents of QueryStatistics, is changed then full_statistics.ser must be recreated with
			The generated file will be saved under this project base folder.

			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("full_statistics.ser"));
			os.writeObject(actualStatistics);
		 */

		final File outputJSONStatistics = new File(outputJSONFilePath);

		assertThat(outputJSONStatistics)
				.exists();

		final QueryStatistics expectedStatistics = restoreQueryStatisticsFromFile("full_statistics.ser");

		assertThat(actualStatistics)
				.isEqualTo(expectedStatistics);
	}

	private String getResourcePathFromClassLoader(final String resource) {
		return getClass().getClassLoader().getResource(resource).getPath();
	}

	private QueryStatistics restoreQueryStatisticsFromFile(final String fileName) throws Exception {
		try (InputStream buffer = new BufferedInputStream(new FileInputStream(getResourcePathFromClassLoader(fileName)))) {
			ObjectInput input = new ObjectInputStream(buffer);

			return (QueryStatistics) input.readObject();
		}
	}
}
