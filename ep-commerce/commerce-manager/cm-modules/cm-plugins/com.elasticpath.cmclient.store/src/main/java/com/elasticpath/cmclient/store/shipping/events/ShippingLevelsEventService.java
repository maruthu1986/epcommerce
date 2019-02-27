/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.shipping.events;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.shipping.ShippingServiceLevel;

/**
 * Shipping service levels filter events service.
 */
public final class ShippingLevelsEventService {
	private final List<ShippingLevelsEventListener> serviceLevelEventListeners = new ArrayList<>();

	/**
	 * Hide singleton constructor.
	 */
	private ShippingLevelsEventService() {

	}

	/**
	 * Singleton accessor.
	 * 
	 * @return ShippingLevelsEventService instance.
	 */
	public static ShippingLevelsEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(ShippingLevelsEventService.class);
	}
	
	/**
	 * Registers a <code>ShippingLevelsEventListener</code> listener.
	 * 
	 * @param listener the shipping levels event listener
	 */
	public void registerShippingLevelListener(final ShippingLevelsEventListener listener) {
		if (!serviceLevelEventListeners.contains(listener)) {
			serviceLevelEventListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>ShipingLevelEventListener</code> listener.
	 * 
	 * @param listener the shipping level event listener
	 */
	public void unregisterShippingLevelEventListener(final ShippingLevelsEventListener listener) {
		if (serviceLevelEventListeners.contains(listener)) {
			serviceLevelEventListeners.remove(listener);
		}
	}

	/**
	 * Notifies all the listeners with a <code>SearchResultEvent</code> event.
	 * 
	 * @param event the search result event
	 */
	public void fireShippingLevelsSearchResultEvent(final SearchResultEvent<ShippingServiceLevel> event) {
		for (final ShippingLevelsEventListener eventListener : serviceLevelEventListeners) {
			eventListener.searchResultsUpdate(event);
		}
	}
}
