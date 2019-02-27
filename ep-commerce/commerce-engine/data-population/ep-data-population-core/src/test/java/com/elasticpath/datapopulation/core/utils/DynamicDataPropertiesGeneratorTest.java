/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Properties;

import org.junit.Test;

public class DynamicDataPropertiesGeneratorTest {

	@Test
	public void testGenerateTimestamp() {
		DynamicDataPropertiesGenerator generator = new DynamicDataPropertiesGenerator();
		Properties timestampProperty = generator.generateDynamicDataProperties();

		assertThat(timestampProperty)
				.isNotNull();

		String timestamp = timestampProperty.getProperty("current.timestamp");
		assertThat(timestamp)
				.as("Did not generate a timestamp")
				.isNotNull();

		Calendar today = Calendar.getInstance();

		assertThat(timestamp)
				.as("The timestamp does not contain the year")
				.contains(String.valueOf(today.get(Calendar.YEAR)));
	}
}
