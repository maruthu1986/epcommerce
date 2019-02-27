/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.event;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;

/**
 * Event service for sending notifications on occurring events.
 */
public final class AdminStoresEventService {

	private final List<StoreEventListener> storeListeners;

	/**
	 * Private constructor following the singleton pattern.
	 */
	private AdminStoresEventService() {
		storeListeners = new ArrayList<>();
	}

	/**
	 * Gets a singleton instance of <code>AdminStoresEventService</code>.
	 *
	 * @return singleton instance of <code>AdminStoresEventService</code>
	 */
	public static AdminStoresEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(AdminStoresEventService.class);
	}

	/**
	 * Notifies all the listeners with an <code>StoreChangeEvent</code> event.
	 *
	 * @param event the store change event
	 */
	public void fireStoreChangeEvent(final ItemChangeEvent<StoreEditorModel> event) {
		for (final StoreEventListener eventListener : storeListeners) {
			eventListener.storeChanged(event);
		}
	}

	/**
	 * Registers a <code>StoreEventListener</code> listener.
	 *
	 * @param listener the store event listener
	 */
	public void registerStoreEventListener(final StoreEventListener listener) {
		if (!storeListeners.contains(listener)) {
			storeListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>StoreEventListener</code> listener.
	 *
	 * @param listener the store event listener
	 */
	public void unregisterStoreEventListener(final StoreEventListener listener) {
		if (storeListeners.contains(listener)) {
			storeListeners.remove(listener);
		}
	}
}
