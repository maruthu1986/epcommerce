/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dto.catalog;

import com.elasticpath.domain.catalog.Catalog;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares Catalogs by their name (case-insensitive).
 */
public class CatalogNameComparator implements Comparator<Catalog>, Serializable {

	private static final long serialVersionUID = 1L;

	/** 
	 * Compares catalogs by name (case-insensitive).
	 * {@inheritDoc}
	 */
	public int compare(final Catalog cat1, final Catalog cat2) {
		return cat1.getName().compareToIgnoreCase(cat2.getName());
	}
}