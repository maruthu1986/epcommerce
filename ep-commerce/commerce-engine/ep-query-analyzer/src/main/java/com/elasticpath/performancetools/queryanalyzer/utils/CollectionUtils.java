/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.utils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Util class with various map/list related methods.
 */
public final class CollectionUtils {

	private CollectionUtils() {
		//util class
	}

	/**
	 * Sort map entries using given comparator.
	 *
	 * @param mapToSort  map to sort.
	 * @param comparator comparator.
	 */
	public static void sortMapEntries(final Map<String, Integer> mapToSort, final Comparator<Map.Entry<String, Integer>> comparator) {
		Map<String, Integer> sortedMap = mapToSort.entrySet()
				.stream()
				.sorted(comparator)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		mapToSort.clear();
		mapToSort.putAll(sortedMap);
	}

	/**
	 * Update overall total and total db/jpa calls per operation.
	 *
	 * @param matcher                sql/jpa matcher.
	 * @param totalCallsPerOperation map operation <-> number of calls per operation.
	 * @param totalCalls             map operation <-> total number of calls
	 */
	public static void updateTotalCallsPerOperation(final Matcher matcher, final Map<String, Integer> totalCallsPerOperation,
													final Map<String, Integer> totalCalls) {
		if (matcher.find()) {
			final String name = matcher.group(1);

			totalCallsPerOperation.merge(name, 1, (calls1, calls2) -> calls1 + calls2);
			totalCalls.merge(name, 1, (calls1, calls2) -> calls1 + calls2);
		}
	}

}
