/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.selector.impl;

import java.util.Collection;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.service.selector.UnpricedShippingCalculationPluginSelector;
import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;

/**
 * Static {@link UnpricedShippingCalculationPluginSelector} implementation.
 */
public class StaticUnpricedShippingCalculationPluginSelectorImpl extends GenericStaticShippingCalculationPluginSelectorImpl
		implements UnpricedShippingCalculationPluginSelector {


	@Override
	public ShippingCalculationPlugin getUnpricedShippingCalculationPlugin(final Collection<? extends ShippableItem> shippableItems,
																		  final ShippingAddress destinationAddress,
																		  final String storeCode) {
		return getShippingCalculationPlugin();

	}

}
