/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.base;


/**
 * Indicates that the class has a global identity field.
 *
 */
public interface GloballyIdentifiable {

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	String getGuid();

	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	void setGuid(String guid);
}
