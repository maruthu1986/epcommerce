/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.shipping.events;

/**
 * Event for notifying about need to reset filter parameters.
 */
public interface IResetFilterListener {

	/**
	 * Is called when filter parameters should be reseted to default values.
	 */
	void resetFilter();
}
