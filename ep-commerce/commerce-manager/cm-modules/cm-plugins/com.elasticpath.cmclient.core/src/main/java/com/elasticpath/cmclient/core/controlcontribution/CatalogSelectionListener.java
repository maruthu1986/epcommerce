/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.controlcontribution;

import com.elasticpath.domain.catalog.Catalog;

/**
 * The interface provides the call bake function for catalog selection changed.
 * 
 *
 */
public interface CatalogSelectionListener {
	
	/**
	 * The mothod will be called when catalog selection changes.
	 * @param catalog the selected catalog
	 */
	void catalogSelected(Catalog catalog);
}
