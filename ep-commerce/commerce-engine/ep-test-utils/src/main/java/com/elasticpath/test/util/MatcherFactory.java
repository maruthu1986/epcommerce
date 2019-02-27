/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.test.util;

import static org.mockito.ArgumentMatchers.argThat;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import junit.framework.AssertionFailedError;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.ArgumentMatcher;

/**
 * Static utility (like Assert) for getting Matchers. 
 */
public final class MatcherFactory {

	private MatcherFactory() {
		// singleton
	}

	/** Matcher that checks objects for equality reflectively.
	 *
	 * @param <T> type of object
	 * @param obj object to check against
	 * @return a matcher
	 */
	public static <T> Matcher<T> reflectivelyEquals(final T obj) {
		return new TypeSafeMatcher<T>() {
			@Override
			public boolean matchesSafely(final T item) {
				try {
					Assert.assertEqualsReflectively(obj, item);
				} catch (AssertionFailedError e) {
					e.printStackTrace(); // NOPMD
					return false;
				}
				return true;
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("Expected ").appendValue(obj);
			}
		};
	}

	/**
	 * Matcher that checks that a list contains exactly the expected values, but in any order.
	 * @param <T> type of list element
	 * @param expectedValues values expected to be in the list
	 * @return a matcher
	 */
	public static <T> Matcher<List<T>> listContaining(final List<T> expectedValues) {
		final Set<T> expectedValuesSet = new HashSet<>(expectedValues);
		return new TypeSafeMatcher<List<T>>() {

			@Override
			public boolean matchesSafely(final List<T> actualValues) {
				return new HashSet<>(actualValues).equals(expectedValuesSet);
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("Expected ").appendValue(expectedValuesSet).appendText(" in any order");
			}
		};

	}

	/**
	 * Registers a {@link Matcher} with Mockito that verifies a {@link Supplier} returns the expected value.
	 * Designed exclusively for use with Mockito's mocking/verification.
	 *
	 * @param expectedValue expected value
	 * @param <T> type of {@link Supplier}
	 * @return {@code null} but registers the generated {@link Matcher} with Mockito.
	 * @see org.mockito.ArgumentMatchers#argThat(ArgumentMatcher).
	 */
	public static <T> Supplier<T> supplierOf(final T expectedValue) {
		return argThat(isSupplierOf(expectedValue));
	}

	/**
	 * Matcher that verifies a {@link Supplier} returns the expected value.
	 *
	 * @param expectedValue expected value
	 * @param <T> the type of {@link Supplier}
	 * @return a {@link Matcher} that verifies a {@link Supplier} returns the expected value.
	 */
	public static <T> ArgumentMatcher<Supplier<T>> isSupplierOf(final T expectedValue) {
		return argument -> Objects.equals(argument.get(), expectedValue);
	}
}
