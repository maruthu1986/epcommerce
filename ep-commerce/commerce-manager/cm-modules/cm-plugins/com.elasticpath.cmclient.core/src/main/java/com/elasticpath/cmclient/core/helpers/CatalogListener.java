/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.catalog.Catalog;

/**
 * Interface of event handler methods to catalog changes.
 */
public interface CatalogListener {
	
	/**
	 * Handle a fired catalog change event.
	 *
	 * @param event a catalog change event
	 */
	void catalogChanged(ItemChangeEvent<Catalog> event);
}