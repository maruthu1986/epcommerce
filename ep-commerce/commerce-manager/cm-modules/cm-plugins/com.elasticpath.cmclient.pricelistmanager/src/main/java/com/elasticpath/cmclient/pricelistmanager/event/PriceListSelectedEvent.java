/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.event;

import java.util.EventObject;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Event signifying that a user has selected a PriceList for editing.
 */
public class PriceListSelectedEvent extends EventObject {

	/** Serial version id. */
	public static final long serialVersionUID = 7000000001L;
	
	/**
	 * Constructor.
	 * @param priceListDescriptor the PriceListDescriptor for the PriceList selected by the user.
	 */
	public PriceListSelectedEvent(final PriceListDescriptorDTO priceListDescriptor) {
		super(priceListDescriptor);
	}
}
