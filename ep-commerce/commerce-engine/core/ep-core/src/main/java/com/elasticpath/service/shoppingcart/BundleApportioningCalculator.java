/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.shoppingcart;

import java.util.Map;

import com.elasticpath.service.shoppingcart.impl.ItemPricing;

/**
 * Calculates apportioned prices for bundle constituents.
 */
public interface BundleApportioningCalculator {
	/**
	 * Apportions an {@link com.elasticpath.service.shoppingcart.impl.ItemPricing} by a collection of
	 * {@link com.elasticpath.service.shoppingcart.impl.ItemPricing}.
	 *
	 * @param pricingToApportion the {@link com.elasticpath.service.shoppingcart.impl.ItemPricing} to be apportioned.
	 * @param constituents a collection of {@link com.elasticpath.service.shoppingcart.impl.ItemPricing} to apportion.
	 * @return a collection {@link com.elasticpath.service.shoppingcart.impl.ItemPricing} that is apportioned.
	 */
	Map<String, ItemPricing> apportion(ItemPricing pricingToApportion, Map<String, ItemPricing> constituents);
}
