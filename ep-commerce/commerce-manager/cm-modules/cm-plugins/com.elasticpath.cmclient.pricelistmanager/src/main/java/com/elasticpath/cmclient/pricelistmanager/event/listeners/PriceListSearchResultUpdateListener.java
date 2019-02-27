/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.event.listeners;

import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Implementors are interested in knowing when new
 * search results for PriceLists are available.
 */
public interface PriceListSearchResultUpdateListener {

	/**
	 * Called when price list search results have been updated.
	 * 
	 * @param event the search result event
	 */
	void searchResultUpdated(SearchResultEvent<PriceListDescriptorDTO> event);
	
}
