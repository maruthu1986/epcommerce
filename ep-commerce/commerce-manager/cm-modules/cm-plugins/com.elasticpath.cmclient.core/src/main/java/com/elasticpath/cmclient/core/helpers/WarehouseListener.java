/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.store.Warehouse;

/**
 * Listener interface used to manage attribute changes.
 */
public interface WarehouseListener {

	/**
	 * Handle changed attribute event.
	 *
	 * @param event the event
	 */
	void warehouseChange(ItemChangeEvent<Warehouse> event);
}
