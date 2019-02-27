/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.event;

import java.util.EventObject;

import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Event signifying that a user has changed a PriceList.
 */
public class PriceListChangedEvent extends EventObject {

	/** Serial version id. */
	public static final long serialVersionUID = 7000000001L;
	
	private EventType eventType = EventType.UPDATE;
	
	/**
	 * Constructor.
	 * @param priceListDescriptor the PriceListDescriptor for the PriceList selected by the user.
	 */
	public PriceListChangedEvent(final PriceListDescriptorDTO priceListDescriptor) {
		super(priceListDescriptor);
	}

	/**
	 * Constructor.
	 * @param priceListDescriptor the PriceListDescriptor for the PriceList selected by the user.
	 * @param eventType event type add/edit/delete
	 */
	public PriceListChangedEvent(final PriceListDescriptorDTO priceListDescriptor, final EventType eventType) {
		super(priceListDescriptor);
		this.eventType = eventType;
	}

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

}
