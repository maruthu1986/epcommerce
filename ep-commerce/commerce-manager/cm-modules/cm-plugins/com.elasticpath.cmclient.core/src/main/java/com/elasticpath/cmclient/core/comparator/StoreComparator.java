/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.comparator;

import java.util.Comparator;

import com.elasticpath.domain.store.Store;

/**
 * Comparator for {@link Store}.
 *
 */
public class StoreComparator implements Comparator<Store> {

	/**
	 * Compares two {@link Store} objects.
	 * @param store1 - left object to compare
	 * @param store2 - right object to compare
	 * @return int - result of compare 
	 */
	public int compare(final Store store1, final Store store2) {
		return store1.getName().compareToIgnoreCase(store2.getName());
	}

}
