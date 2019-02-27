/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.selector;

import java.util.Collection;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;

/**
 * Priced shipping calculation provider selector interface.
 */
public interface PricedShippingCalculationPluginSelector {

	/**
	 * Returns {@link ShippingCalculationPlugin} from given params.
	 *
	 * @param shippableItems     shippable items
	 * @param destinationAddress destination address
	 * @param storeCode          store code
	 * @return ShippingCalculationPlugin
	 */
	ShippingCalculationPlugin getPricedShippingCalculationPlugin(Collection<? extends PricedShippableItem> shippableItems,
																 ShippingAddress destinationAddress,
																 String storeCode);
}
