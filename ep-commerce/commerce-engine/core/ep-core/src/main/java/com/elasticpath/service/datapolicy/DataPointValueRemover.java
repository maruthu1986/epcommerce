/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy;

import java.util.Collection;

import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * The data point value remover has two responsibilities:
 *
 * 1. remove a list of data point values
 * 2. check the applicability of the remover based on data point location
 */
public interface DataPointValueRemover {

	/**
	 * Remove the list of data point values.
	 *
	 * @param dataPointValues the list of data point value UidPks.
	 * @return the number of removed data point values
	 */
	int removeValues(Collection<DataPointValue> dataPointValues);

	/**
	 * Check if removing the underlying value is applicable for a given data point.
	 *
	 * @param dataPointLocation the data point to check.
	 * @return true if removing the underlying value is applicable.
	 */
	default boolean isApplicableTo(String dataPointLocation) {
		return getSupportedLocation().equals(dataPointLocation);
	}

	/**
	 * Returns the location supported by the remover.
	 *
	 * @return the location.
	 */
	String getSupportedLocation();
}
