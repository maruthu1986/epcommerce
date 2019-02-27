/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Holds a pair of objects.
 *
 * @param <FIRST> the type of the first object
 * @param <SECOND> the type of the second object
 */
@SuppressWarnings("PMD.ShortClassName")
public class Pair<FIRST, SECOND> implements Serializable {
	private final FIRST first;

	private final SECOND second;

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 610000000L;

	/**
	 * Create a new object pair.
	 *
	 * @param first the first object
	 * @param second the second object
	 */
	public Pair(final FIRST first, final SECOND second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Creates a new pair for the given values.
	 *
	 * @param <FIRST> the generic type of the first parameter
	 * @param <SECOND> the generic type of the second parameter
	 * @param first first
	 * @param second second
	 * @return the pair
	 */
	@SuppressWarnings("PMD.ShortMethodName")
	public static <FIRST, SECOND> Pair<FIRST, SECOND> of(final FIRST first, final SECOND second) {
		return new Pair<>(first, second);
	}

	/**
	 * @return the first object
	 */
	public FIRST getFirst() {
		return first;
	}

	/**
	 * @return the second object
	 */
	public SECOND getSecond() {
		return second;
	}


	/**
	 * @return the hashCode base on the hashCodes of first and second.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	/**
	 * @param other the other object to test for equality.
	 * @return true if both object's first and second are equal, false otherwise.
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || this.getClass() != other.getClass()) {
			return false;
		}

		Pair<?, ?> otherPair = (Pair<?, ?>) other;

		return Objects.equals(first, otherPair.first)
			&& Objects.equals(second, otherPair.second);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("first", first).append("second", second).toString();
	}
}
