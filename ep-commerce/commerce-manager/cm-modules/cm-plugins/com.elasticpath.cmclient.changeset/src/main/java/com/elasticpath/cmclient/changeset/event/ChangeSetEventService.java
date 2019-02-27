/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.changeset.event;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.cmclient.core.CmSingletonUtil;

/**
 * Event service for sending notifications on occurring events.
 */
public final class ChangeSetEventService {

	private final List<ChangeSetEventListener> changeSetListeners = new ArrayList<>();;

	private final List<ChangeSetSearchEventListener> changeSetSearchListeners = new ArrayList<>();

	private ChangeSetEventService() {
		//private constructor
	}

	/**
	 * Gets a singleton INSTANCE of <code>FulfillmentEventService</code>.
	 *
	 * @return singleton INSTANCE of <code>FulfillmentEventService</code>
	 */
	public static ChangeSetEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(ChangeSetEventService.class);
	}

	/**
	 * Notifies all the listeners with a change event.
	 *
	 * @param event the change set modification event
	 */
	public void fireChangeSetModificationEvent(final ItemChangeEvent<ChangeSet> event) {
		for (final ChangeSetEventListener eventListener : changeSetListeners) {
			eventListener.changeSetModified(event);
		}
	}

	/**
	 * Fire change set search event.
	 *
	 * @param searchResultEvent the search result event
	 */
	public void fireChangeSetSearchEvent(final SearchResultEvent<ChangeSet> searchResultEvent) {
		Display.getDefault().asyncExec(() -> {
			for (final ChangeSetSearchEventListener eventListener : changeSetSearchListeners) {
				eventListener.changeSetSearchUpdated(searchResultEvent);
			}
		});
	}

	/**
	 * Registers a <code>ChangeSetEventListener</code> listener.
	 *
	 * @param listener the event listener
	 */
	public void registerChangeSetEventListener(final ChangeSetEventListener listener) {
		if (!changeSetListeners.contains(listener)) {
			changeSetListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>ChangeSetEventListener</code> listener.
	 *
	 * @param listener the event listener
	 */
	public void unregisterChangeSetEventListener(final ChangeSetEventListener listener) {
		if (changeSetListeners.contains(listener)) {
			changeSetListeners.remove(listener);
		}
	}

	/**
	 * Registers a <code>ChangeSetSearchEventListener</code> listener.
	 *
	 * @param listener the event listener
	 */
	public void registerChangeSetSearchEventListener(final ChangeSetSearchEventListener listener) {
		if (!changeSetSearchListeners.contains(listener)) {
			changeSetSearchListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>ChangeSetSearchEventListener</code> listener.
	 *
	 * @param listener the event listener
	 */
	public void unregisterChangeSetSearchEventListener(final ChangeSetSearchEventListener listener) {
		if (changeSetSearchListeners.contains(listener)) {
			changeSetSearchListeners.remove(listener);
		}
	}

}
