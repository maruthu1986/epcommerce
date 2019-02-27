/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.event.listeners;

import java.util.EventListener;

import com.elasticpath.cmclient.pricelistmanager.event.PriceListChangedEvent;

/**
 * Implementors are interested in knowing when a user has changed a PriceList.
 */
public interface PriceListChangedEventListener extends EventListener {

	/**
	 * Called when a PriceListChanged event is observed.
	 * 
	 * @param event the PriceListChangedEvent instance
	 */
	void priceListChanged(PriceListChangedEvent event);
}