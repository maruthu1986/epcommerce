/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;

/**
 * Listener interface used to manage CartItemModifierGroupListener changes.
 */
public interface CartItemModifierGroupListener {
	/**
	 * Handle changed brand event.
	 *
	 * @param event the event
	 */
	void groupChange(ItemChangeEvent<CartItemModifierGroup> event);
}
