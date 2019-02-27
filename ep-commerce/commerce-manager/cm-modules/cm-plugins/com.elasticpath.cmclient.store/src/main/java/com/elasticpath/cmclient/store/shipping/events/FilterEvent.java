/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.shipping.events;

import java.util.List;

import com.elasticpath.domain.shipping.ShippingServiceLevel;

/**
 * Event for notifying about filter changes.
 */
public class FilterEvent {
	private final List<ShippingServiceLevel> shippingLevels;

	/**
	 * Create event and store ShippingServiceLevel's list.
	 * 
	 * @param shippingLevels ShippingServiceLevel's list to store.
	 */
	public FilterEvent(final List<ShippingServiceLevel> shippingLevels) {
		this.shippingLevels = shippingLevels;
	}

	/**
	 * Access method for the shippingLevels property.
	 * 
	 * @return the current value of the shippingLevels property
	 */
	public List<ShippingServiceLevel> getShippingLevels() {
		return shippingLevels;
	}
}
