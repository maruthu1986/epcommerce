/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.event.EventOriginatorType;
import com.elasticpath.domain.event.OrderEventHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderEvent;

/**
 * Static class to provide some util functions for the order auditing.
 *
 */
public final class OrderEventCmHelper {

	private OrderEventCmHelper() {
		// Do nothing
	}
	
	/**
	 * Get the <code>OrderEventHelper</code> to log the order events.
	 * @return the order event helper to log the order events.
	 */
	public static OrderEventHelper getOrderEventHelper() {
		return (OrderEventHelper) ServiceLocator.getService(ContextIdNames.ORDER_EVENT_HELPER);

	}
	
	/**
	 * Initial the event originator for order auditing.
	 * @param order the given order
	 */
	public static void initForOrderAuditing(final Order order) {
		EventOriginatorHelper eventOriginatorHelper = ServiceLocator.getService(
				ContextIdNames.EVENT_ORIGINATOR_HELPER);
		order.setModifiedBy(eventOriginatorHelper.getCmUserOriginator(LoginManager.getCmUser()));

	}
	
	/**
	 * Get the string value who generated the event. 
	 * @param orderEvent the given order event
	 * @return string value who generated the event.
	 */
	public static String getCreatedBy(final OrderEvent orderEvent) {
		if (orderEvent.getCreatedBy() != null) {
			return orderEvent.getCreatedBy().getUserName();
		}			
		if (orderEvent.getOriginatorType() == EventOriginatorType.CUSTOMER) {
			return FulfillmentMessages.get().Customer;
		}			
		return FulfillmentMessages.get().System;
	}

	/**
	 * Get the string value who generated the event. 
	 * @param eventOriginator the given eventOriginator
	 * @return string value who generated the event.
	 */
	public static String getCreatedBy(final EventOriginator eventOriginator) {
		if (eventOriginator.getCmUser() != null) {
			return eventOriginator.getCmUser().getUserName();
		}			
		if (eventOriginator.getType() == EventOriginatorType.CUSTOMER) {
			return FulfillmentMessages.get().Customer;
		}			
		return FulfillmentMessages.get().System;
	}
	
	/**
	 * Check whether an event is originated by a CmUser.
	 * @param orderEvent the order event
	 * @return true if the event is originated by a CmUser
	 */
	public static boolean isCmUserGeneratedEvent(final OrderEvent orderEvent) {
		return orderEvent.getOriginatorType() == EventOriginatorType.CMUSER
			|| orderEvent.getOriginatorType() == EventOriginatorType.WSUSER;
	}

	/**
	 * Check whether an event is originated by System.
	 * @param orderEvent the order event
	 * @return true if the event is originated by System
	 */
	public static boolean isSystemGeneratedEvent(final OrderEvent orderEvent) {
		return orderEvent.getOriginatorType() == EventOriginatorType.CUSTOMER
			|| orderEvent.getOriginatorType() == EventOriginatorType.SYSTEM;
	}

}
