/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.event;

import java.util.EventObject;

/**
 * Event signifying that a user has triggered a search for Price Lists.
 */
public class PriceListSearchEvent extends EventObject {
	
	/** Serial version id. */
	public static final long serialVersionUID = 7000000001L;
	
	/**
	 * Constructor.
	 * @param source the search criteria object
	 * TODO: This should take in a SearchCriteria object rather than a generic Object
	 */
	public PriceListSearchEvent(final Object source) {
		super(source);
	}

}
