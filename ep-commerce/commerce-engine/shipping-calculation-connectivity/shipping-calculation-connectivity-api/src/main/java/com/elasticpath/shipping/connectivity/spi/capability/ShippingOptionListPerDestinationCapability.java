/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.spi.capability;

import java.util.List;
import java.util.Locale;

import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Capability of listing shipping option per destination which present unpriced.
 */
public interface ShippingOptionListPerDestinationCapability extends ShippingCalculationCapability {

	/**
	 * Returns list of {@link ShippingOption} per destination and store.
	 *
	 * @param destinationAddress the destination address.
	 * @param storeCode          the store code
	 * @param locale             the locale
	 * @return list of {@link ShippingOption}.
	 */
	List<ShippingOption> getUnpricedShippingOptions(ShippingAddress destinationAddress,
													String storeCode,
													Locale locale);

}
