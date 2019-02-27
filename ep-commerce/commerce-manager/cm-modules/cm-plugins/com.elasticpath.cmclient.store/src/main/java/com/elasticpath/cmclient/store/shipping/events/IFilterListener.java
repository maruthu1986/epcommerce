/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.shipping.events;

/**
 * Implement to be notified about filter updates.
 */
public interface IFilterListener {

	/**
	 * Notifies about filter update.
	 * 
	 * @param event stores filtered list of shipping levels.
	 */
	void filterUpdated(FilterEvent event);
}
