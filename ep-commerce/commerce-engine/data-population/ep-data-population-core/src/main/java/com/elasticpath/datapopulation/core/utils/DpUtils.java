/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.supercsv.io.ICsvListReader;

/**
 * A simple utility-class for cross-cutting common functionality.
 */
public final class DpUtils {

	/**
	 * This class is intended as as a static utility class, so it is not intended to be instantiated.
	 */
	private DpUtils() {
		super();
	}

	/**
	 * Returns a simple String containing the exception's message, or an empty String if no exception is given.
	 * <p>
	 * Spring-Shell doesn't display nested exception information on the console when an exception is thrown containing a nested exception.
	 * The CLI tool tends to wrap root exceptions inside a {@link com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException}
	 * and so unless we include the root exception's message in the
	 * {@link com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException}'s
	 * message the actual error is not communicated with the user.
	 *
	 * @param exception the exception to inspect.
	 * @return a simple String containing the exception's message, or an empty String if no exception is given.
	 */
	public static String getNestedExceptionMessage(final Exception exception) {
		String result = "";

		if (exception != null) {
			result = "Nested exception:\n\t" + exception.getMessage();
		}

		return result;
	}

	/**
	 * For the given {@link ICsvListReader} implementation, this method firs creates a {@link List&lt;String&gt;}, then passes both objects to
	 * {@link #readAllEntries(org.supercsv.io.ICsvListReader, java.util.Collection)} to read all contents into the newly constructed list before
	 * returning it.
	 *
	 * @param csvReader the reader to read.
	 * @return a list populated with the contents read from the reader.
	 * @throws IOException if the reader throws an exception when attempting to read from it.
	 */
	public static List<String> readAllEntries(final ICsvListReader csvReader) throws IOException {
		final List<String> result = new ArrayList<>();

		readAllEntries(csvReader, result);

		return result;
	}

	/**
	 * For the given {@link ICsvListReader} implementation, this method repeatedly reads from it, adding the contents read to the collection
	 * passed in, until no more entries are available ({@link org.supercsv.io.ICsvListReader#read()} returns null).
	 *
	 * @param csvReader   the reader to read.
	 * @param destination the collection to populate.
	 * @throws IOException if the reader throws an exception when attempting to read from it.
	 */
	public static void readAllEntries(final ICsvListReader csvReader, final Collection<String> destination) throws IOException {
		List<String> lineEntries;
		while ((lineEntries = csvReader.read()) != null) {
			destination.addAll(lineEntries);
		}
	}

	/**
	 * Returns the size of the given {@link java.util.Map}, returning 0 if null is passed in.
	 * Mainly used for logging to avoid log statements from throwing NPEs.
	 *
	 * @param map the {@link java.util.Map} implementation to check
	 * @param <K> the type of key contained by this {@link java.util.Map}.
	 * @param <V> the type of value contained by this {@link java.util.Map}.
	 * @return the size of the given {@link java.util.Map}, returning 0 if null is passed in.
	 */
	public static <K, V> int size(final Map<K, V> map) {
		int result = 0;

		if (MapUtils.isNotEmpty(map)) {
			result = map.size();
		}

		return result;
	}
}
