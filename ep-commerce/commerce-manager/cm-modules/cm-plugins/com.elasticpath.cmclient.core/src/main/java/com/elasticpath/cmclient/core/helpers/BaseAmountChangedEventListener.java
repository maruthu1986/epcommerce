/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Implementors are interested in knowing when a user has changed a base amount.
 */
public interface BaseAmountChangedEventListener {
	/**
	 * Called when a PriceListChanged event is observed.
	 * 
	 * @param event the PriceListChangedEvent instance
	 */
	void baseAmountChanged(ItemChangeEvent<BaseAmountDTO> event);
}
