/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.spi.capability;

import java.util.List;
import java.util.Locale;

import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Capability of listing all shipping option to present unpriced {@link ShippingOption}s.
 */
public interface ShippingOptionListAllCapability extends ShippingCalculationCapability {

	/**
	 * Return list of {@link ShippingOption} contains all available ones.
	 *
	 * @param storeCode     the store code
	 * @param locale        the locale
	 * @return list of {@link ShippingOption}.
	 */
	List<ShippingOption> getAllShippingOptions(String storeCode, Locale locale);

}
