/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.helpers;

import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.catalog.Product;

/**
 * Advanced search product listener interface. Clients should implement it and register with 
 * the {@link com.elasticpath.cmclient.advancedsearch.service.AdvancedSearchEventService} for getting a notification on changes.
 */
public interface AdvancedSearchProductListener {

	/**
	 * Callback for advanced search result found events.
	 * 
	 * @param event the event object
	 */
	void productAdvancedSearchResultReturned(SearchResultEvent<Product> event);

}
