/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.misc;

/**
 * Represents a domain object that can be ordered.
 */
public interface Orderable {
	
	/**
	 * Get the ordering number.
	 * 
	 * @return the ordering number
	 */
	int getOrdering();

	/**
	 * Set the ordering number.
	 * 
	 * @param ordering the ordering number
	 */
	void setOrdering(int ordering);
}
