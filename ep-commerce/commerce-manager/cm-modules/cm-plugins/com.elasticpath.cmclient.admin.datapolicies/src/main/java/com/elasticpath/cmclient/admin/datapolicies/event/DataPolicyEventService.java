/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.event;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.datapolicy.DataPolicy;

/**
 * Event service for sending notifications on occurring events.
 */
public final class DataPolicyEventService {

	private final List<DataPolicyEventListener> dataPolicyEventListeners;

	/**
	 * Private constructor following the singleton pattern.
	 */
	private DataPolicyEventService() {
		dataPolicyEventListeners = new ArrayList<>();
	}

	/**
	 * Gets a singleton instance of <code>DataPolicyEventService</code>.
	 *
	 * @return singleton instance of <code>DataPolicyEventService</code>
	 */
	public static DataPolicyEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(DataPolicyEventService.class);
	}

	/**
	 * Notifies all the listeners with an <code>ItemChangeEvent</code> event.
	 *
	 * @param event the datapolicy change event
	 */
	public void fireDataPolicyChanged(final ItemChangeEvent<DataPolicy> event) {
		for (final DataPolicyEventListener eventListener : dataPolicyEventListeners) {
			eventListener.dataPolicyChanged(event);
		}
	}

	/**
	 * Registers a <code>ItemChangeEvent</code> listener.
	 *
	 * @param listener the datapolicy event listener
	 */
	public void registerDataPolicyEventListener(final DataPolicyEventListener listener) {
		if (!dataPolicyEventListeners.contains(listener)) {
			dataPolicyEventListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>ItemChangeEvent</code> listener.
	 *
	 * @param listener the datapolicy event listener
	 */
	public void unregisterDataPolicyEventListener(final DataPolicyEventListener listener) {
		dataPolicyEventListeners.remove(listener);
	}
}
