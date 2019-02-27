/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.commons.util.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Utility methods for null-safe immutable collections, maps.
 */
public final class Immutables {

	private Immutables() {
		// Do not instantiate this class
	}

	/**
	 * Create an immutable map from the given source map. Return an empty map is the source is null.
	 *
	 * @param source the source map
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return an immutable copy of the map, or empty map if the source is null
	 */
	public static <K, V> Map<K, V> immutableMapFrom(final Map<K, V> source) {
		if (source == null) {
			return ImmutableMap.of();
		}

		return ImmutableMap.copyOf(source);
	}

	/**
	 * Create an immutable list from the given source list. Return an empty list if the source is null.
	 *
	 * @param source the source list
	 * @param <E> the element type
	 * @return an immutable copy of the list, or empty list if the source is null
	 */
	public static <E> List<E> immutableListFrom(final Collection<E> source) {
		if (source == null) {
			return ImmutableList.of();
		}

		return ImmutableList.copyOf(source);
	}

}
