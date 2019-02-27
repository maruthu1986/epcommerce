/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.search;

import java.util.Collection;
import java.util.Set;

import com.elasticpath.persistence.api.Persistable;

/**
 * The interface for safe seach uids. 
 * The implementation class is used for search criteria 
 * to filter out the catalog/store/warehouse which the user does not have permission
 *
 */
public interface SafeSearchUids {

	/**
	 * Extract and add the property from each instance of the collection.
	 *   
	 * @param collection the source collection
	 */
	void extractAndAdd(Collection< ? extends Persistable> collection);

	/**
	 * Extract and add the property from the object.
	 * 
	 * @param object the source object
	 */
	void extractAndAdd(Persistable object);

	/**
	 * Get the set of retrieved uids.
	 * 
	 * @return the set of retrieved uids
	 */
	Set<Long> asSet();
}
