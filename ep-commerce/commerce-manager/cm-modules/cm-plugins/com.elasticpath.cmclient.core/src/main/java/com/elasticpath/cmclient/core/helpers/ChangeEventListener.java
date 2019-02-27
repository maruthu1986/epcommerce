/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;

/**
 * 
 * Event listener for changes to objects which can be in change set.
 */
public interface ChangeEventListener {
	
	/**
	 * Called when an entity which can be in a change set has been changed in some way.
	 *
	 * @param event the event
	 */
	void changeSetChanged(ItemChangeEvent< ? > event);
}
