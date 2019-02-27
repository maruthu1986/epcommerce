/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.admin.customers.event;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.customer.CustomerGroup;

/**
 * Event service for sending notifications on occurring events.
 */
public final class CustomerSegmentEventService {

	private final List<CustomerSegmentEventListener> customerSegmentListeners;

	/**
	 * Private constructor following the singleton pattern.
	 */
	private CustomerSegmentEventService() {
		customerSegmentListeners = new ArrayList<>();
	}

	/**
	 * Gets a session instance of <code>CustomerSegmentEventService</code>.
	 * 
	 * @return session instance of <code>CustomerSegmentEventService</code>
	 */
	public static CustomerSegmentEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(CustomerSegmentEventService.class);
	}

	/**
	 * Notifies all the listeners with an <code>CustomerSegmentEventService</code> event.
	 * 
	 * @param event the customer change event
	 */
	public void fireCustomerSegmentChangeEvent(final ItemChangeEvent<CustomerGroup> event) {
		for (final CustomerSegmentEventListener eventListener : customerSegmentListeners) {
			eventListener.customerSegmentChanged(event);
		}
	}

	/**
	 * Registers a <code>CustomerSegmentEventListener</code> listener.
	 * 
	 * @param listener the customer segment event listener
	 */
	public void registerCustomerSegmentEventListener(final CustomerSegmentEventListener listener) {
		if (!customerSegmentListeners.contains(listener)) {
			customerSegmentListeners.add(listener);
		}
	}

	/**
	 * Unregisters a <code>CustomerSegmentEventListener</code> listener.
	 * 
	 * @param listener the customer segment event listener
	 */
	public void unregisterCustomerSegmentEventListener(final CustomerSegmentEventListener listener) {
		if (customerSegmentListeners.contains(listener)) {
			customerSegmentListeners.remove(listener);
		}
	}

}
