/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.eventlistener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.registry.ObjectRegistry;

/**
 * Manages the coolbar listeners.
 */
public class CoolbarEventManager implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<CoolbarListener> listeners = new ArrayList<>();

	/**
	 * Returns the singleton instance.
	 *
	 * @return the singleton instance
	 */
	public static CoolbarEventManager getInstance() {
		CoolbarEventManager result = (CoolbarEventManager) ObjectRegistry.getInstance().getObject(CoolbarEventManager.class.getName());
		if (result == null) {
			result = new CoolbarEventManager();
			ObjectRegistry.getInstance().putObject(CoolbarEventManager.class.getName(), result);
		}
		return result;
	}

	/**
	 * Removes the listener.
	 *
	 * @param listener the listener to be removed
	 */
	public void removeListener(final CoolbarListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Adds the listener.
	 *
	 * @param listener the listener to be added
	 */
	public void addListener(final CoolbarListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Request updates to the coolbar listeners.
	 */
	public void updateRequest() {
		for (CoolbarListener listener : listeners) {
			listener.updateRequested();
		}
	}
}
