/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.catalog.Category;

/**
 * Interface of event handler methods to category changes.
 */
public interface CategoryListener {
	
	/**
	 * Handle a fired category change event.
	 *
	 * @param event a category change event
	 */
	void categoryChanged(ItemChangeEvent<Category> event);
	
	/**
	 * Callback for search result found events.
	 * 
	 * @param event the event object
	 */
	void categorySearchResultReturned(SearchResultEvent<Category> event);

	
	
	
}
