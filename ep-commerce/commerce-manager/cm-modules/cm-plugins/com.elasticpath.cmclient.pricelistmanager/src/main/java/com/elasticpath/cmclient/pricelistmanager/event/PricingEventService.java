/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.event;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.pricelistmanager.event.listeners.PriceListChangedEventListener;
import com.elasticpath.cmclient.pricelistmanager.event.listeners.PriceListSearchEventListener;
import com.elasticpath.cmclient.pricelistmanager.event.listeners.PriceListSearchResultUpdateListener;
import com.elasticpath.cmclient.pricelistmanager.event.listeners.PriceListSelectedEventListener;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Manages events related to Price List Management.
 */
public final class PricingEventService {

	private final List<PriceListSearchEventListener> searchEventListeners = new ArrayList<>();
	private final List<PriceListSelectedEventListener> priceListSelectedEventListeners = new ArrayList<>();
	private final List<PriceListSearchResultUpdateListener> searchResultUpdateListeners = new ArrayList<>();
	private final List<PriceListChangedEventListener> priceListChangedEventListeners = new ArrayList<>();
	
	private PricingEventService() {
		//do nothing
	}
	
	/**
	 * @return the singleton instance of the event service
	 */
	public static PricingEventService getInstance() {
	    return CmSingletonUtil.getSessionInstance(PricingEventService.class);

	}
	
	/**
	 * Adds a PriceListSearchEventListener.
	 * @param listener the listener
	 */
	public void addPriceListSearchEventListener(final PriceListSearchEventListener listener) {
		searchEventListeners.add(listener);
	}
	
	/**
	 * Removes a PriceListSearchEventListener.
	 * @param listener the listener
	 */
	public void removePriceListSearchEventListener(final PriceListSearchEventListener listener) {
		searchEventListeners.remove(listener);
	}
	
	/**
	 * Adds a PriceListSelectedEventListener.
	 * @param listener the listener
	 */
	public void addPriceListSelectedEventListener(final PriceListSelectedEventListener listener) {
		priceListSelectedEventListeners.add(listener);
	}
	
	/**
	 * Removes a PriceListSelectedEventListener.
	 * @param listener the listener
	 */
	public void removePriceListSelectedEventListener(final PriceListSelectedEventListener listener) {
		priceListSelectedEventListeners.remove(listener);
	}
	
	/**
	 * Adds a PriceListSearchResultUpdateListener.
	 * @param listener the listener
	 */
	public void addPriceListSearchResultUpdateListener(final PriceListSearchResultUpdateListener listener) {
		searchResultUpdateListeners.add(listener);
	}
	
	/**
	 * Removes a PriceListSearchResultUpdateListener.
	 * @param listener the listener
	 */
	public void removePriceListSearchResultUpdateListener(final PriceListSearchResultUpdateListener listener) {
		searchResultUpdateListeners.remove(listener);
	}

	/**
	 * Adds a PriceListChangedEventListener.
	 * @param listener the listener
	 */
	public void addPriceListChangedEventListener(final PriceListChangedEventListener listener) {
		priceListChangedEventListeners.add(listener);
	}
	
	/**
	 * Removes a PriceListChangedEventListener.
	 * @param listener the listener
	 */
	public void removePriceListChangedEventListener(final PriceListChangedEventListener listener) {
		priceListChangedEventListeners.remove(listener);
	}	
	
	/**
	 * Fires a PriceListSearchEvent.
	 * @param event the event to fire
	 */
	public void fireSearchEvent(final PriceListSearchEvent event) {
		for (PriceListSearchEventListener listener : searchEventListeners) {
			listener.searchPriceList(event);
		}
	}
	
	/**
	 * Fires a PriceListSelectedEvent.
	 * @param event the event to fire
	 */
	public void fireSelectedEvent(final PriceListSelectedEvent event) {
		for (PriceListSelectedEventListener listener : priceListSelectedEventListeners) {
			listener.priceListSelected(event);
		}
	}
	
	/**
	 * Fires a SearchResultEvent.
	 * @param event the event to fire
	 */
	public void fireSearchResultUpdatedEvent(final SearchResultEvent<PriceListDescriptorDTO> event) {
		for (PriceListSearchResultUpdateListener listener : searchResultUpdateListeners) {
			listener.searchResultUpdated(event);
		}
	}
	
	/**
	 * Fires a PriceListChangedEvent.
	 * @param event the event to fire
	 */
	public void fireChangedEvent(final PriceListChangedEvent event) {
		for (PriceListChangedEventListener listener : priceListChangedEventListeners) {
			listener.priceListChanged(event);
		}
	}
}
