/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.helpers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elasticpath.cmclient.core.CmSingletonUtil;


/**
 * An event hub implementation for controlling events and handling.
 */
public final class EventManager {
	
	/**
	 * @return {@link EventManager} instance.
	 */
	public static EventManager getInstance() {
		return CmSingletonUtil.getSessionInstance(EventManager.class);
	}

	private final Map<Object, List<EventListener>> listeners = new ConcurrentHashMap<>();

	private EventManager() {
		// no implementation
	}

	/**
	 * Adds a event listener based on the source.
	 * 
	 * @param source the source
	 * @param listener an event listener
	 */
	public void addListener(final Object source, final EventListener listener) {
		List<EventListener> eventListeners = listeners.computeIfAbsent(source, key -> new CopyOnWriteArrayList<>());
		eventListeners.add(listener);
	}

	/**
	 * Fires an event.
	 * 
	 * @param source the source
	 * @param event an event
	 */
	public void fireEvent(final Object source, final EventObject event) {
		List<EventListener> eventListeners = listeners.get(source);
		if (eventListeners == null) {
			return;
		}

		for (EventListener listener : eventListeners) {
			if (isPropertyChange(event, listener)) {
				((PropertyChangeListener) listener).propertyChange((PropertyChangeEvent) event);
			}
		}
	}

	private boolean isPropertyChange(final EventObject event, final EventListener listener) {
		return listener instanceof PropertyChangeListener && event instanceof PropertyChangeEvent;
	}

	/**
	 * Removes the given listener.
	 * 
	 * @param source the source
	 * @param listener an {@link EventListener}
	 */
	public void removeListener(final Object source, final EventListener listener) {
		List<EventListener> eventListeners = listeners.get(source);
		if (eventListeners == null) {
			listeners.remove(source);
		} else {
			eventListeners.remove(listener);
		}
	}
}
