/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.search;

import java.util.Collection;
import java.util.Set;

/**
 * The interface for safe seach codes. 
 * The implementation class is used for search criteria 
 * to filter out the catalog/store/warehouse which the user does not have permission
 *
 */
public interface SafeSearchCodes {

	/**
	 * Extract and add the property from each instance of the collection.
	 *   
	 * @param collection the source collection
	 * @param propertyName the property name to retrieve the value
	 */
	void extractAndAdd(Collection<?> collection, String propertyName);

	/**
	 * Extract and add the property from the object.
	 * 
	 * @param object the source object
	 * @param propertyName the property name to retrieve the value
	 */
	void extractAndAdd(Object object, String propertyName);

	/**
	 * Get the set of retrieved properties.
	 * 
	 * @return the set of retrieved properties
	 */
	Set<String> asSet();
}
