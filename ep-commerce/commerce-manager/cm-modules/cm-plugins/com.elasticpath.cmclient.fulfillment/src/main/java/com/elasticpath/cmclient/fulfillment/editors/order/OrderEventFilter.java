/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.domain.order.OrderEvent;

/**
 * The order event filter.
 */
public class OrderEventFilter extends ViewerFilter {

	/** All event types. */
	public static final int EVENT_TYPE_ALL = 0;
	/** CSR originated only. */
	public static final int EVENT_TYPE_CSR = 1;
	/** System originated only. */
	public static final int EVENT_TYPE_SYSTEM = 2;
	
	private int eventType;
	private String eventOriginator;
	
	/** 
	 * Check whether the given element matches with the filter.
	 * @param viewer the viewer
	 * @param parentElement the parent element
	 * @param element the element
	 * @return true if the element matches with the filter.
	 */
	@Override
	public boolean select(final Viewer viewer, final Object parentElement,
						  final Object element) {
		OrderEvent orderEvent = (OrderEvent) element;
		return matchEventType(orderEvent) && matchEventOriginator(orderEvent);
	}

	/**
	 * Check whether the order event match with the given event type.
	 * @param orderEvent the order event
	 * @return true if the order event match with the given event type
	 */
	public boolean matchEventType(final OrderEvent orderEvent) {
		switch (eventType) {
		case EVENT_TYPE_ALL:
			return true;
		case EVENT_TYPE_CSR:
			return OrderEventCmHelper.isCmUserGeneratedEvent(orderEvent);
		case EVENT_TYPE_SYSTEM:
			return OrderEventCmHelper.isSystemGeneratedEvent(orderEvent);
		default:
			return false;
		}
	}

	private boolean matchEventOriginator(final OrderEvent orderEvent) {
		return eventOriginator == null
				|| eventOriginator.equals(FulfillmentMessages.get().Event_Originator_All)
				|| eventOriginator.equals(OrderEventCmHelper.getCreatedBy(orderEvent));
	}

	/**
	 * @return the eventType
	 */
	public int getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(final int eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the eventOriginator
	 */
	public String getEventOriginator() {
		return eventOriginator;
	}

	/**
	 * @param eventOriginator the eventOriginator to set
	 */
	public void setEventOriginator(final String eventOriginator) {
		this.eventOriginator = eventOriginator;
	}
	
	
}
