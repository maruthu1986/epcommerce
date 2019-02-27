/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.controller;

import java.util.EventObject;

import com.elasticpath.cmclient.core.eventlistener.UIEventListener;
/**
 * BaseController
 * UI Controller interface.
 * @param <V> event object.
 * @param <T> type.
 */
public interface BaseController< V extends EventObject, T extends UIEventListener< V >> {
	
	/**
	 * Add event listener.
	 * @param eventListener listener to add
	 */
	void addListener(T eventListener);
	
	/**
	 * Remove event listener.
	 * @param eventListener listener to delete
	 */
	void removeListener(T eventListener);
	
	/**
	 * Remove all listeners.
	 */
	void removeAllListeners();
	
	/**
	 * Event object.
	 * @param eventObject event object
	 */
	void fireEvent(V eventObject);
}
