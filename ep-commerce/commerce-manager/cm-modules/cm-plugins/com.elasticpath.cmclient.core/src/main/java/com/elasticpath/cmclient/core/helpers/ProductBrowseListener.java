/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.BrowseResultEvent;
import com.elasticpath.domain.catalog.Product;

/**
 * Product listener interface. 
 * Clients should implement it and register with the {@link com.elasticpath.cmclient.core.service.CatalogEventService} 
 * for getting a notification on changes.
 */
public interface ProductBrowseListener {

	/**
	 * Callback for search result found events.
	 * 
	 * @param event the event object
	 */
	void productBrowseResultReturned(BrowseResultEvent<Product> event);

}
