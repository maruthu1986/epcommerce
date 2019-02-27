/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.List;

import com.elasticpath.persistence.api.Persistable;

/**
 * Defines items returned from a search.
 * 
 * @param <T> The type of items returned.
 */
public interface SearchItemsLocator<T extends Persistable> {

	/**
	 * Gets the list of items corresponding to the given <code>uidList</code>.
	 * 
	 * @param uidList a list of UIDs
	 * @return a list of items corresponding to the given UIDs
	 */
	List<T> getItems(List<Long> uidList);

	/**
	 * Fires an event that the given <code>itemList</code> are results returned from a search.
	 * 
	 * @param itemList the list of items representing returned results
	 * @param startIndex the start index used to get the items
	 * @param totalFound the total number of items found
	 */
	void fireItemsUpdated(List<T> itemList, int startIndex, int totalFound);
	
}