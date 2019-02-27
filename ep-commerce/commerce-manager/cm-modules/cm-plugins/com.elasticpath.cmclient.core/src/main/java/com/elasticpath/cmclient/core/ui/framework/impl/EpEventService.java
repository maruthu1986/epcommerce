/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;

/**
 * Event service class for firing resize events.
 */
final class EpEventService {


	private EpEventService() {
		super();
		this.listeners = new ArrayList<EpResizeListener>();
	}

	/**
	 * Gets the singleton instance.
	 * 
	 * @return EpEventService
	 */
	public static EpEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(EpEventService.class);
	}

	private final List<EpResizeListener> listeners;

	/**
	 * Adds new resize event listener.
	 * 
	 * @param listener the listener implementation
	 */
	public void addResizeListener(final EpResizeListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Fires new resize event.
	 */
	public void fireResizeEvent() {
		final Iterator<EpResizeListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			iter.next().resize();
		}
	}

	/**
	 * Removes the specified listener.
	 * 
	 * @param listener the listener to be removed
	 */
	public void removeResizeListener(final EpResizeListener listener) {
		this.listeners.remove(listener);
	}

}

/**
 * Resize listener.
 */
interface EpResizeListener {
	/**
	 * Callback method for resize events.
	 */
	void resize();
}
