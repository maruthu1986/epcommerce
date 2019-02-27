/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping;

import java.util.Currency;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;

/**
 * An interface that describes input information used by {@link com.elasticpath.service.shipping.transformers.PricedShippableItemsTransformer} to
 * transform Shopping Items into {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItem} objects.
 */
public interface ShippableItemsPricing {
	/**
	 * Returns the {@link Function} used to retrieve the corresponding {@link ShoppingItemPricingSnapshot} from a given {@link ShoppingItem}.
	 *
	 * @return the pricing Function.
	 */
	Function<ShoppingItem, ShoppingItemPricingSnapshot> getShoppingItemPricingFunction();

	/**
	 * The currency the items are priced in.
	 *
	 * @return the currency the items are priced in.
	 */
	Currency getCurrency();

	/**
	 * The calculated cart subtotal discount applied.
	 *
	 * @return the subtotal discount.
	 */
	Money getSubtotalDiscount();

	/**
	 * An optional {@link Predicate} to filter any {@link ShoppingItem} objects to only transform items that pass the filter.
	 *
	 * @return a {@link Predicate} to filter all {@link ShoppingItem} objects before transforming them.
	 */
	Optional<Predicate<ShoppingItem>> getShippableItemPredicate();
}
