/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.base.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;

/**
 * Static utility class to simplify working with Streams introduced in Java 8.
 */
public final class StreamUtils {
	private StreamUtils() {
		// Static utils class so no object instantiation is allowed.
	}

	/**
	 * Implements a {@link Collector} to be able to use instead of {@link java.util.stream.Collectors#toList()} to get back an immutable list.
	 * Note: Guava provides a similar method in a later version - {@code ImmutableList.toImmutableList()} - so once we've upgraded the supported Guava
	 * version this method should be removed in favour of that one.
	 *
	 * @param <E> the list element type.
	 * @return A {@link Collector} to be used in Java Stream processing to return an immutable list.
	 */
	public static <E> Collector<E, List<E>, List<E>> toImmutableList() {
		return Collector.of(ArrayList::new, List::add, (left, right) -> {
			left.addAll(right);
			return left;
		}, Collections::unmodifiableList);
	}
}
