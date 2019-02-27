/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping;

import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;

/**
 * An interface that describes input information used by {@link com.elasticpath.service.shipping.transformers.PricedShippableItemTransformer} to
 * transform an individual {@link com.elasticpath.domain.shoppingcart.ShoppingItem} into a corresponding
 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItem} object.
 */
public interface ShippableItemPricing {
	/**
	 * Get the corresponding pricing information for the given {@link com.elasticpath.domain.shoppingcart.ShoppingItem}.
	 *
	 * @return the corresponding pricing, must not be {@code null}.
	 */
	ShoppingItemPricingSnapshot getShoppingItemPricingSnapshot();

	/**
	 * Get the apportioned subtotal discount per unit for this particular item.
	 *
	 * @return the apportioned subtotal discount per unit for this particular item.
	 */
	Money getApportionedItemSubtotalUnitDiscount();

	/**
	 * Get the apportioned subtotal discount for all quantity of this particular item.
	 *
	 * @return the apportioned subtotal discount for all quantity of this particular item.
	 */
	Money getApportionedItemSubtotalDiscount();
}
