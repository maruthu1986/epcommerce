/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service.selector.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;

/**
 * Generic static shipping calculation plugin selector.
 */
public class GenericStaticShippingCalculationPluginSelectorImpl {

	private List<ShippingCalculationPlugin> shippingCalculationPluginList;

	/**
	 * Returns simply the exact one {@link ShippingCalculationPlugin} loaded.
	 *
	 * @return the loaded {@link ShippingCalculationPlugin}.
	 */
	ShippingCalculationPlugin getShippingCalculationPlugin() {

		checkArgument(shippingCalculationPluginList != null && !shippingCalculationPluginList.isEmpty(),
				"ShippingCalculationPlugin list cannot be empty or null.");

		// verify only one plugin loaded.
		checkArgument(shippingCalculationPluginList.size() == 1,
				"Expects only one ShippingCalculationPlugin. Found multiple ShippingCalculationPlugin.[%s]",
				StringUtils.join(shippingCalculationPluginList.stream()
										 .filter(Objects::nonNull)
										 .map(ShippingCalculationPlugin::getName)
										 .toArray(String[]::new), ',')
		);

		final ShippingCalculationPlugin shippingCalculationPlugin = shippingCalculationPluginList.get(0);

		checkArgument(shippingCalculationPlugin != null, "ShippingCalculationPlugin cannot be null.");

		return shippingCalculationPlugin;

	}

	public void setShippingCalculationPluginList(final List<ShippingCalculationPlugin> shippingCalculationPluginList) {
		this.shippingCalculationPluginList = shippingCalculationPluginList;
	}

}
