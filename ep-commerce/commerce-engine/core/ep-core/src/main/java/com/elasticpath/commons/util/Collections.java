/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.commons.util;

import java.util.SortedMap;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * A supplement to <code>java.util.Collections</code>.
 *
 * @deprecated Use Guava instead
 */
@Deprecated
public final class Collections {

	private Collections() {
		// static class
	}

	/**
	 * @param <T> the set type
	 * @return an empty immutable {@link SortedSet}
	 * @deprecated use Guava's ImmutableSortedSet.of() instead
	 * @see {@link ImmutableSortedSet#of()}
	 */
	@Deprecated
	public static <T> SortedSet<T> emptySortedSet() {
		return ImmutableSortedSet.of();
	}


	/**
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return empty immutable {@link SortedMap}
	 * @deprecated use Guava's ImmutableSortedMap.of() instead
	 * @see {@link ImmutableSortedMap#of()}
	 */
	@Deprecated
	public static <K, V> SortedMap<K, V> emptySortedMap() {
		return ImmutableSortedMap.of();
	}

}
