/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.eventlistener;

import java.util.EventListener;
import java.util.EventObject;

/**
 * A tagging interface that all event listener interfaces must extend.
 * @param <T> type.
 */
public interface UIEventListener<T extends EventObject> extends EventListener {
	
	/**
	 * All event listeners must implement this  method. 
	 * @param eventObject The event object.
	 */
	void onEvent(T eventObject);

}
