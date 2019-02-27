/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Search result event service.
 */
public final class NavigationEventService {

	private final List<NavigationEventListener> listeners;

	/**
	 * Private constructor following the singleton pattern.
	 */
	public NavigationEventService() {
		super();
		this.listeners = new ArrayList<NavigationEventListener>();
	}

	/**
	 * Notifies all the listeners with a <code>NavigationEvent</code> event.
	 * 
	 * @param event the NavigationEvent to fire
	 */
	public void fireNavigationEvent(final NavigationEvent event) {
		for (final NavigationEventListener listener : this.listeners) {
			listener.navigationChanged(event);
		}
	}

	/**
	 * Registers a <code>NavigationEventListener</code> listener.
	 * 
	 * @param listener the NavigationEventListener
	 */
	public void registerNavigationEventListener(final NavigationEventListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>NavigationEventListener</code> listener.
	 *
	 * @param listener the NavigationEventListener
	 */
	public void unregisterNavigationEventListener(final NavigationEventListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

}
