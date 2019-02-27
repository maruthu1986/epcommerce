/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.selector.impl;

import java.util.Collection;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.service.selector.PricedShippingCalculationPluginSelector;
import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;

/**
 * Static {@link PricedShippingCalculationPluginSelector} implementation.
 */
public class StaticPricedShippingCalculationPluginSelectorImpl extends GenericStaticShippingCalculationPluginSelectorImpl
		implements PricedShippingCalculationPluginSelector {

	@Override
	public ShippingCalculationPlugin getPricedShippingCalculationPlugin(final Collection<? extends PricedShippableItem> shippableItems,
																		final ShippingAddress destinationAddress,
																		final String storeCode) {
		return super.getShippingCalculationPlugin();
	}

}
