/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.comparator;

import java.util.Comparator;

import com.elasticpath.domain.catalog.Catalog;

/**
 * Comparator for {@link Catalog}.
 *
 */
public class CatalogComparator implements Comparator<Catalog> {

	/**
	 * Compares two {@link Catalog} objects.
	 * @param object1 - left object to compare
	 * @param object2 - right object to compare
	 * @return int - result of compare 
	 */
	public int compare(final Catalog object1, final Catalog object2) {
		return object1.getName().compareToIgnoreCase(object2.getName());
	}

}
