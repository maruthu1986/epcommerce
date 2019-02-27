/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.event;

import java.util.EventObject;

import com.elasticpath.common.pricing.service.BaseAmountFilterExt;

/**
 * Base amount search event.
 *
 */
public class BaseAmountSearchEvent extends EventObject {
	
	/** Serial version id. */
	public static final long serialVersionUID = 20091208L;
	
	/**
	 * Constructor.
	 * @param source the search criteria object
	 */
	public BaseAmountSearchEvent(final BaseAmountFilterExt source) {
		super(source);
	}	

}
