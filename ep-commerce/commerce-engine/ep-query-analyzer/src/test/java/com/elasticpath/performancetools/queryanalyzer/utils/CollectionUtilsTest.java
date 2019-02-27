/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.performancetools.queryanalyzer.beans.QueryStatistics;

/**
 * Test class for {@link CollectionUtils}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectionUtilsTest {

	private static final String OPERATION1 = "operation1";

	/**
	 * Ensures that statistics maps have correct type (LinkedHashMap).
	 */
	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	public void shouldSortMapEntriesByValueInDescendingOrder() {
		List<Map<String, Integer>> statisticMaps = initializeStatisticMaps();

		for (Map<String, Integer> map : statisticMaps) {
			CollectionUtils.sortMapEntries(map, getMapSorterByValueInDescendingOrder());

			assertThat(map)
					.containsKeys("op2", "op3", "op1");

			assertThat(map)
					.containsValues(3, 2, 1);
		}
	}

	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	public void shouldUpdateTotalCallsAndTotalCallsPerOperationWhenOperationIsFound() {
		final Matcher matcher = Pattern.compile(".*?(" + OPERATION1 + ").*?").matcher("bla bla " + OPERATION1 + " bla bla");
		final Map<String, Integer> totalCalls = new LinkedHashMap<>();
		totalCalls.put(OPERATION1, 10);

		final Map<String, Integer> totalCallsPerOperation = new LinkedHashMap<>();
		totalCallsPerOperation.put(OPERATION1, 5);

		CollectionUtils.updateTotalCallsPerOperation(matcher, totalCallsPerOperation, totalCalls);

		assertThat(totalCalls)
				.containsValue(11);
		assertThat(totalCallsPerOperation)
				.containsValue(6);
	}

	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	public void shouldUpdateTotalCallsAndTotalCallsPerOperationWhenOperationIsNotFound() {
		final Matcher matcher = Pattern.compile(".*?(" + OPERATION1 + ").*?").matcher("bla bla something bla bla");
		final Map<String, Integer> totalCalls = new LinkedHashMap<>();
		totalCalls.put(OPERATION1, 10);

		final Map<String, Integer> totalCallsPerOperation = new LinkedHashMap<>();
		totalCallsPerOperation.put(OPERATION1, 5);

		CollectionUtils.updateTotalCallsPerOperation(matcher, totalCallsPerOperation, totalCalls);

		assertThat(totalCalls)
				.containsValue(10);
		assertThat(totalCallsPerOperation)
				.containsValue(5);
	}

	private Comparator<Map.Entry<String, Integer>> getMapSorterByValueInDescendingOrder() {
		return (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue());
	}

	private List<Map<String, Integer>> initializeStatisticMaps() {
		final List<Map<String, Integer>> maps = new ArrayList<>();
		final QueryStatistics statistics = new QueryStatistics();

		maps.add(createMapEntries(statistics.getTotalDBCallsPerTable()));
		maps.add(createMapEntries(statistics.getTotalJPACallsPerEntity()));
		maps.add(createMapEntries(statistics.getTotalDBCallsPerOperation()));
		maps.add(createMapEntries(statistics.getTotalJPACallsPerOperation()));
		maps.add(createMapEntries(statistics.getTotalDBCallExeTimePerOperationMs()));

		return maps;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private Map<String, Integer> createMapEntries(final Map<String, Integer> map) {
		map.put("op1", 1);
		map.put("op2", 3);
		map.put("op3", 2);

		return map;
	}
}
