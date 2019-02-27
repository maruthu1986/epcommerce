/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * A class that defines the properties used by default in the data, and provides methods to generate the values at runtime.
 * Extension projects can sub-class this class to provide additional dynamic data properties.
 */
public class DynamicDataPropertiesGenerator {
	/**
	 * The property key used in property placeholders when it should be replaced by the current timestamp.
	 */
	public static final String CURRENT_TIMESTAMP_PROPERTY_KEY = "current.timestamp";

	/**
	 * The format of the timestamp that should be used in generated timestamps (see {@link #getTimestampFormat()} ).
	 */
	public static final String TIMESTAMP_FORMAT = "yyyyMMdd.HHmmss";

	/**
	 * Returns a {@link java.util.Properties} object containing all dynamic data property values by delegating to
	 * {@link #populateDynamicDataProperties(java.util.Map)}.
	 *
	 * @return a {@link java.util.Properties} object containing all dynamic data property values.
	 */
	public Properties generateDynamicDataProperties() {
		final Properties result = new Properties();
		populateDynamicDataProperties(result);
		return result;
	}

	/**
	 * Populates the given {@link java.util.Map} with all the dynamic data properties supported by this class.
	 *
	 * @param map the map to update with the current dynamic data property values.
	 */
	// Use Map<? super String, ? super String> to allow not just Map<String, String> but also Properties objects to be passed in
	public void populateDynamicDataProperties(final Map<? super String, ? super String> map) {
		map.put(DynamicDataPropertiesGenerator.CURRENT_TIMESTAMP_PROPERTY_KEY, generateTimestamp());
	}

	// Individual dynamic property generator methods

	/**
	 * Returns a timestamp for the current date.
	 *
	 * @return a timestamp String for the current date.
	 */
	public String generateTimestamp() {
		return generateTimestamp(new Date());
	}

	/**
	 * Generates a timestamp for the date given. Calls {@link #getTimestampFormat()} to determine what format the timestamp should be in.
	 *
	 * @param date the date that the timestamp should be generated for.
	 * @return a timestamp for the date given.
	 */
	public String generateTimestamp(final Date date) {
		return DateFormatUtils.format(date, getTimestampFormat());
	}

	// Helper methods

	/**
	 * Defines the timestamp format to use, returing {@link #TIMESTAMP_FORMAT}; sub-classes can override if necessary.
	 *
	 * @return the timestamp format in use.
	 */
	protected String getTimestampFormat() {
		return TIMESTAMP_FORMAT;
	}
}
