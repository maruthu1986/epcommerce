/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.util.ServiceUtil;

/**
 * This class is responsible for keeping a counter.
 * Stores two maps
 * <p>
 * First: encoded value to the id
 * Second: id to value
 * <p>
 * Ex. having encoded value get id and then use it to get original value.
 *
 * NOTE: initializeEncodingMarkers should be called before using this manager.
 */
@SuppressWarnings({"checkstyle:magicnumber", "PMD"})
public final class TestIdMapManager {

	private static final String BEGINNING_ENCODING_MARKER = "\uFEFF"; //zero width no-break space
	private static final String END_ENCODING_MARKER = "\u180E"; //mongolian vowel separator

	private static final String ZERO = "\u2061"; //function application
	private static final String ONE = "\u2063"; //invisible separator

	private static final String TEMPLATE_STRING = "[0]";
	private static final String QUOTE = "'";

	private static Map<String, String> minifiedMap = new HashMap<>();
	private static int counter;

	/**
	 * Constructor.
	 */
	private TestIdMapManager() {
		//empty
	}

	/**
	 * Sets Encoding Markers that JS side should use to decode values.
	 */
	public static void initializeEncodingMarkers() {
		String callClientJavaScriptFunction = "EPTest.setEncodingMarkers("
			+ QUOTE + BEGINNING_ENCODING_MARKER + QUOTE + ","
			+ QUOTE + END_ENCODING_MARKER + QUOTE + ");";
		JavaScriptExecutor executor = ServiceUtil.getRWTService(JavaScriptExecutor.class);
		executor.execute(callClientJavaScriptFunction);
	}

	public static Map<String, String> getMinifiedMap() {
		return minifiedMap;
	}

	/**
	 * Sets field value when properties are bound.
	 * @param clazz class
	 * @param result result
	 * @param field field
	 * @param fieldName fieldName
	 * @param value value
	 * @param <T> class type
	 * @throws IllegalAccessException field exception
	 */
	public static <T> void setFieldValueBasedTestMode(final Class<T> clazz, final T result, final Field field,
		final String fieldName, final String value) throws IllegalAccessException {

		//If we are in test mode and string substitution is required for the value then do not include it in the map
		//Some places don't just set the value but process it for example as a template. Ignore encoding them
		//As a result those values are language dependent
		if (UITestUtil.isEnabled() && !value.contains(TEMPLATE_STRING)) {
			final String shortId = createInvisibleBinaryId();
			final String encodedValue = TestIdMapManager.tagAsEncoded(shortId);

			final String qualifiedFieldName = clazz.getName() + "." + fieldName;
			final String filteredValue = value.replace("&", CoreMessages.EMPTY_STRING);

			minifiedMap.put(shortId, qualifiedFieldName);

			field.set(result, encodedValue + filteredValue);
		} else {
			field.set(result, value);
		}
	}

	/**
	 * Marks the value as encoded.
	 * @param value value to mark
	 * @return marked value
	 */
	private static String tagAsEncoded(final String value) {
		return BEGINNING_ENCODING_MARKER + value + END_ENCODING_MARKER;
	}

	private static String createInvisibleBinaryId() {
		//Convert counter to the invisible sequence
		String value = Integer.toBinaryString(counter)
			.replace("0", ZERO)
			.replace("1", ONE);

		counter++;

		return value;
	}
}