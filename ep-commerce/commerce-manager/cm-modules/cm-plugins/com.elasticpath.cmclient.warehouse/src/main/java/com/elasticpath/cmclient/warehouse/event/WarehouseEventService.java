/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.event;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.order.OrderReturn;

/**
 * Event service for sending notifications on occurring events.
 */
public final class WarehouseEventService {

	private final List<OrderReturnEventListener> orderReturnListeners;

	/**
	 * Private constructor following the singleton pattern.
	 */
	private WarehouseEventService() {
		super();
		orderReturnListeners = new ArrayList<>();
	}

	/**
	 * Gets a session instance of <code>WarehouseEventService</code>.
	 * 
	 * @return session instance of <code>WarehouseEventService</code>
	 */
	public static WarehouseEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(WarehouseEventService.class);
	}

	/**
	 * Notifies all the listeners with an <code>SearchResultEvent</code> event.
	 * 
	 * @param event the search result event
	 */
	public void fireOrderReturnSearchResultEvent(final SearchResultEvent<OrderReturn> event) {
		for (final OrderReturnEventListener eventListener : orderReturnListeners) {
			eventListener.searchResultsUpdate(event);
		}
	}

	/**
	 * Notifies all the listeners with an <code>OrderReturnChangeEvent</code> event.
	 * 
	 * @param event the order change event
	 */
	public void fireOrderReturnChangeEvent(final OrderReturnChangeEvent event) {
		for (final OrderReturnEventListener eventListener : orderReturnListeners) {
			eventListener.orderReturnChanged(event);
		}
	}

	/**
	 * Registers a <code>WarehouseEventListener</code> listener.
	 * 
	 * @param listener the warehouse event listener
	 */
	public void registerOrderReturnEventListener(final OrderReturnEventListener listener) {
		if (!orderReturnListeners.contains(listener)) {
			orderReturnListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>WarehouseEventListener</code> listener.
	 * 
	 * @param listener the warehouse event listener
	 */
	public void unregisterOrderReturnEventListener(final OrderReturnEventListener listener) {
		if (orderReturnListeners.contains(listener)) {
			orderReturnListeners.remove(listener);
		}
	}
}
