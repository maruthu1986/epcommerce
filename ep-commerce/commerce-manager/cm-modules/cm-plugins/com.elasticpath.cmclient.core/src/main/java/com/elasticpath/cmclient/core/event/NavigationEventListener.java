/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.event;

/**
 * Implemented by objects wishing to be informed of navigation bar clicks.
 */
public interface NavigationEventListener {

	/**
	 * Does something in response to a navigation event.
	 * @param event the navigation event
	 */
	void navigationChanged(NavigationEvent event);
}
