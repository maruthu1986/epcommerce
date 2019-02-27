/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.service;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeEventListener;

/**
 * Event service for changes to object which can be in change set.
 * 
 */
public final class ChangeSetEventService {

	
	private final List<ChangeEventListener> changeEventListeners = new ArrayList<ChangeEventListener>();
	
	private ChangeSetEventService() {
	}
	
	/**
	 * Get the session instance for the service.
	 *
	 * @return the service
	 */
	public static ChangeSetEventService getInstance() {
		return  CmSingletonUtil.getSessionInstance(ChangeSetEventService.class);
	}
	
	/**
	 * Notify of change set event.
	 *
	 * @param event the event
	 */
	public void notifyChangeSetEvent(final ItemChangeEvent< ? > event) {
		for (ChangeEventListener listener : changeEventListeners) {
			listener.changeSetChanged(event);
		}
	}

	/**
	 * Add change event listener.
	 *
	 * @param listener the listener
	 */
	public void addChangeEventListener(final ChangeEventListener listener) {
		changeEventListeners.add(listener);
	}
	
	/**
	 * Remove change event listener.
	 *
	 * @param listener the listener
	 */
	public void removeChangeEventListener(final ChangeEventListener listener) {
		changeEventListeners.remove(listener);
	}
}

