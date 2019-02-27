/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.catalog.CategoryType;

/**
 * Listener interface used to manage category type changes.
 */
public interface CategoryTypeListener {

	/**
	 * Handle changed category type event.
	 *
	 * @param event the event
	 */
	void categoryTypeChange(ItemChangeEvent<CategoryType> event);
}
