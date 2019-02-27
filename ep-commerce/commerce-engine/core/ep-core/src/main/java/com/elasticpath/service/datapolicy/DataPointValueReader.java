/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy;

import java.util.Collection;
import java.util.Map;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * The data point value reader has 3 responsibilities:
 *
 * 1. read the underlying data point db value
 * 2. check the applicability of the reader based on data point location
 * 3. validate data point key, whenever possible
 */
public interface DataPointValueReader {
	/**
	 * Check if reading the underlying value is applicable to a given data point location.
	 * Each reader must provide the info about supported location.
	 *
	 * @param dataPointLocation the data point location to check.
	 * @return true if reading the underlying value is applicable.
	 */
	default boolean isApplicableTo(String dataPointLocation) {
		return getSupportedLocation().equals(dataPointLocation);
	}

	/**
	 * Check if given {@link DataPoint}'s data key exists in the db.
	 *
	 * @param dataKey the data key to be validated.
	 *
	 * @return true if data key exists in the db.
	 */
	default boolean validateKey(String dataKey) {
			return getSupportedFields().containsKey(dataKey);
	}

	/**
	 * Returns the location supported by the reader.
	 *
	 * @return the location.
	 */
	String getSupportedLocation();

	/**
	 * Returns the map of reader's supported FIELD_MSG_KEY - DB_FIELD_NAME pairs.
	 * @return the map.
	 */
	Map<String, String> getSupportedFields();

	/**
	 * Find grained method for reading multiple customer's data point values at once within a given date range.
	 *
	 * @param customerGuid the customer GUID.
	 * @param dataPoints the list of data point to read values for.
	 * @return the list of data point values.
	 */
	Collection<DataPointValue> readValues(String customerGuid, Collection<DataPoint> dataPoints);
}
