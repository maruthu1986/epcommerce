/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.spi.capability;

import java.util.List;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Capability of listing shipping option to present price.
 */
public interface ShippingCostCalculationCapability extends ShippingCalculationCapability {

	/**
	 * Returns list of priced {@link ShippingOption} with calculated shipping cost.
	 *
	 * @param pricedShippableItemContainer the priced shippable item container
	 * @return list of {@link ShippingOption}.
	 */
	List<ShippingOption> getPricedShippingOptions(PricedShippableItemContainer<?> pricedShippableItemContainer);
}
