/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Product SKU listener interface. Clients should implement it and register with 
 * the {@link com.elasticpath.cmclient.core.service.CatalogEventService} 
 * for getting a notification on changes.
 */
public interface ProductSkuListener {

	/**
	 * Callback for search result found events.
	 * 
	 * @param event the event object
	 */
	void productSkuSearchResultReturned(SearchResultEvent<ProductSku> event);

	/**
	 * Callback for productSKU changed event.
	 * 
	 * @param event the event object
	 */
	void productSkuChanged(ItemChangeEvent<ProductSku> event);

}
