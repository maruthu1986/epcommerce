/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.component;

import java.util.EventListener;

/**
 * ActionEventListener.
 * @param <V> event object type
 */
public interface ActionEventListener<V> extends EventListener {

	/**
	 * Affected object for this event.
	 * @param object object
	 */
	void onEvent(V object);
}
