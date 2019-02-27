/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.spi.capability;

import java.util.List;

import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Capability of listing shipping option to present unpriced {@link ShippingOption}s.
 */
public interface ShippingOptionListCapability extends ShippingCalculationCapability {

	/**
	 * Return {@link ShippingOption} per unpriced shippable item container.
	 *
	 * @param unpricedShippableItemContainer the unpriced shippable item container.
	 * @return the list of {@link ShippingOption}.
	 */
	List<ShippingOption> getUnpricedShippingOptions(ShippableItemContainer<?> unpricedShippableItemContainer);
}
