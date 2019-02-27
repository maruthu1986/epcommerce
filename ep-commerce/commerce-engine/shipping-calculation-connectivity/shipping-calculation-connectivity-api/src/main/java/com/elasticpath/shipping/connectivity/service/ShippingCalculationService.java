/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service;

import java.util.Locale;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;

/**
 * Provides shipping calculation service.
 */
public interface ShippingCalculationService {

	/**
	 * Calculates unpriced shipping options that are applicable to the given {@link ShippableItemContainer}.
	 *
	 * @param shippableItemContainer shippable item container to calculate the unpriced shipping options for.
	 * @return the {@link ShippingCalculationResult}, never {@code null}.
	 */
	ShippingCalculationResult getUnpricedShippingOptions(ShippableItemContainer<?> shippableItemContainer);

	/**
	 * Returns priced shipping options.
	 *
	 * @param pricedShippableItemContainer the priced shippable item container.
	 * @return {@link ShippingCalculationResult}, never {@code null}.
	 */
	ShippingCalculationResult getPricedShippingOptions(PricedShippableItemContainer<?> pricedShippableItemContainer);

	/**
	 * Get all shipping options.
	 *
	 * @param storeCode the store code
	 * @param locale    the locale
	 * @return {@link ShippingCalculationResult}, never {@code null}.
	 */
	ShippingCalculationResult getAllShippingOptions(String storeCode, Locale locale);

	/**
	 * Gets shipping options per destination.
	 *
	 * @param destAddress the destination address
	 * @param storeCode   the store code
	 * @param locale      the locale
	 * @return {@link ShippingCalculationResult}, never {@code null}.
	 */
	ShippingCalculationResult getUnpricedShippingOptions(ShippingAddress destAddress, String storeCode, Locale locale);
}
