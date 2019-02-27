/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.catalog.Product;

/**
 * Product listener interface.
 * Clients should implement it and register with the {@link com.elasticpath.cmclient.core.service.CatalogEventService}
 * for getting a notification on changes.
 */
public interface ProductListener {

	/**
	 * Callback for search result found events.
	 * 
	 * @param event the event object
	 */
	void productSearchResultReturned(SearchResultEvent<Product> event);
	
	/**
	 * Callback for product changed event.
	 * 
	 * @param event the event object
	 */
	void productChanged(ItemChangeEvent<Product> event);
}
