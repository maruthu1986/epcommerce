/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.selector;

import java.util.Collection;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;

/**
 * Unpriced shipping calculation provider selector interface.
 */
public interface UnpricedShippingCalculationPluginSelector {

	/**
	 * Returns {@link ShippingCalculationPlugin} from given params.
	 *
	 * @param shippableItems     the shippable items
	 * @param destinationAddress the destination address
	 * @param storeCode          the store code
	 * @return {@link ShippingCalculationPlugin}
	 */
	ShippingCalculationPlugin getUnpricedShippingCalculationPlugin(Collection<? extends ShippableItem> shippableItems,
																   ShippingAddress destinationAddress,
																   String storeCode);
}
