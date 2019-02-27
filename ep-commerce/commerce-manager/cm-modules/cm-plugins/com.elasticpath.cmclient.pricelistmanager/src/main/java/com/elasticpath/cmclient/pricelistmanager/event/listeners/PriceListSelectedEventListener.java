/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.event.listeners;

import java.util.EventListener;

import com.elasticpath.cmclient.pricelistmanager.event.PriceListSelectedEvent;

/**
 * Implementors are interested in knowing when a user has selected a PriceList
 * (e.g. to be edited).
 */
public interface PriceListSelectedEventListener extends EventListener {

	/**
	 * Called when a PriceListSelected event is observed.
	 * @param event the event
	 */
	void priceListSelected(PriceListSelectedEvent event);
}
