/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.datapolicy;

import com.elasticpath.persistence.api.Entity;

/**
 * Represents Data Point.
 */
public interface DataPoint extends Entity {

	/**
	 * Return the name.
	 *
	 * @return the name.
	 */
	String getName();

	/**
	 * Set the name.
	 *
	 * @param name the name to set.
	 */
	void setName(String name);

	/**
	 * Return the data location.
	 *
	 * @return the data location.
	 */
	String getDataLocation();

	/**
	 * Set the data location.
	 *
	 * @param dataLocation the data location to set.
	 */
	void setDataLocation(String dataLocation);

	/**
	 * Return the data key.
	 *
	 * @return the data key.
	 */
	String getDataKey();

	/**
	 * Set the data key.
	 *
	 * @param dataKey the data key to set.
	 */
	void setDataKey(String dataKey);

	/**
	 * Return the description key.
	 *
	 * @return the description key.
	 */
	String getDescriptionKey();

	/**
	 * Set the description key.
	 *
	 * @param descriptionKey the description key to set.
	 */
	void setDescriptionKey(String descriptionKey);

	/**
	 * Return true if data point is removable.
	 *
	 * @return true if data point is removable.
	 */
	boolean isRemovable();

	/**
	 * Set removable.
	 *
	 * @param removable removable
	 */
	void setRemovable(boolean removable);

	/**
	 * Get expanded location which is built from dataLocation-dataKey.
	 * This location is a unique identifier for data point.
	 *
	 * @return expanded location.
	 */
	String getExpandedLocation();
}
