/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.event.listeners;

import com.elasticpath.cmclient.pricelistmanager.event.BaseAmountSearchEvent;

/**
 * Implementors are interested in knowing when a search for base amounts
 * has been requested.
 */
public interface BaseAmountSearchEventListener {
	
	/**
	 * Called when a search for base amounts.
	 * @param event the search event, which contain search criteria
	 */	
	void searchBaseAmounts(BaseAmountSearchEvent event);

}
