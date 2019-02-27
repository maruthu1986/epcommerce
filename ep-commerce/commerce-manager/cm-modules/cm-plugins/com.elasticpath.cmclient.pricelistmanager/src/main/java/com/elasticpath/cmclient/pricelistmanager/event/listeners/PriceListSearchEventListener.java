/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.event.listeners;

import java.util.EventListener;

import com.elasticpath.cmclient.pricelistmanager.event.PriceListSearchEvent;

/**
 * Implementors are interested in knowing when a search for price lists
 * has been requested.
 */
public interface PriceListSearchEventListener extends EventListener {

	/**
	 * Called when a search for price lists is requested.
	 * @param event the search event, which may contain search criteria
	 */
	void searchPriceList(PriceListSearchEvent event);
}
