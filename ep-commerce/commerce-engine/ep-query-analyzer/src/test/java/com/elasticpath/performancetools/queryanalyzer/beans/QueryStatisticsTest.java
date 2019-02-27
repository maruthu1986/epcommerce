/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.beans;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Query statistics test.
 */
public class QueryStatisticsTest {

	private final QueryStatistics queryStatistics = new QueryStatistics();

	@Test
	public void shouldProperlyFormatTime() {
		final long timeToFormatMs = 61001;

		String expected = "1 minute(s) 1 second(s) 1 millis";
		String actual = queryStatistics.getFormattedTime(timeToFormatMs);

		assertThat(actual)
				.as("Given time is not properly formatted")
				.isEqualTo(expected);
	}
}
