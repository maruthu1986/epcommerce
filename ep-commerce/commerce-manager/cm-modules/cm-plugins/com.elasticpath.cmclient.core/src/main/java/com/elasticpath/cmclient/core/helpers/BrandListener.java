/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.catalog.Brand;

/**
 * Listener interface used to manage brand changes.
 */
public interface BrandListener {

	/**
	 * Handle changed brand event.
	 *
	 * @param event the event
	 */
	void brandChange(ItemChangeEvent<Brand> event);
}
