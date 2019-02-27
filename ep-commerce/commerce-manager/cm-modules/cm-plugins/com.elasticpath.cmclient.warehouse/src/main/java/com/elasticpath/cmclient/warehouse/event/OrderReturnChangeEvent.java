/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.event;

import java.util.EventObject;

import com.elasticpath.domain.order.OrderReturn;

/**
 * Event object for the <code>OrderReturn</code> change event.
 */
public class OrderReturnChangeEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private final OrderReturn orderReturn;

	/**
	 * The Constructor.
	 * 
	 * @param source the source of the event
	 * @param orderReturn the <code>OrderReturn</code> object
	 */
	public OrderReturnChangeEvent(final Object source, final OrderReturn orderReturn) {
		super(source);
		this.orderReturn = orderReturn;
	}

	/**
	 * Returns the <code>OrderReturn</code> object.
	 * 
	 * @return Order Return
	 */
	public OrderReturn getOrderReturn() {
		return orderReturn;
	}

}
