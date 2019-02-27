/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.openjpa.sampledata;

/**
 * Test Interface for domain objects that hold transient data.
 */
public interface TransientDataHolder {

	/**
	 * Get the transient data.
	 *
	 * @return the transient data
	 */
	String getTransientData();

	/**
	 * Set the transient data.
	 *
	 * @param transientData the data to set
	 */
	void setTransientData(String transientData);
}
