/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.event;

import java.util.EventObject;

/**
 * Event object for the promotions change event.
 */
public class CouponChangeEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the event object.
	 * 
	 * @param source the source of the event
	 */
	public CouponChangeEvent(final Object source) {
		super(source);
	}
}
