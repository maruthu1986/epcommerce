/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.tax.domain;

import java.util.Map;

/**
 * Represents an entitlement that exempts an Order from sales taxes.
 */
public interface TaxExemption {
	/**
	 * Returns the id number associated with this exemption.
	 * @return an id string
	 */
	String getExemptionId();

	/**
	 * Set the id number associated with this exemption.
	 * @param exemptionId the id
	 */
	void setExemptionId(String exemptionId);

	/**
	 * Adds the <code>String</code> value data with the given <code>String</code> key.
	 *
	 * @param key the data's key
	 * @param value the data's value
	 */
	void addData(String key,  String value);

	/**
	 * Retrieves the data with the given key.
	 * @param key the key of the data to lookup
	 * @return the data with the given key; null if there is no data associated with the key
	 */
	String getData(String key);

	/**
	 * Returns the data map for this tax exemption entity.
	 *
	 * @return mapping of the data values
	 */
	Map<String, String> getAllData();
}
