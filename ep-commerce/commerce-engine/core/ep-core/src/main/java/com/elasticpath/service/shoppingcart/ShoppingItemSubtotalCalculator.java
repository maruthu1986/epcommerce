/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart;

import java.util.Collection;
import java.util.Currency;
import java.util.function.Function;
import java.util.stream.Stream;

import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;

/**
 * Calculates the subtotal of {@link ShoppingItem}.
 * Excludes tax for tax-exclusive regions. Includes tax in tax-inclusive regions.
 */
public interface ShoppingItemSubtotalCalculator {

	/**
	 * Calculates the subtotal of the shopping items, including item discounts, if present.
	 *
	 * @param shoppingItems the shopping items to evaluate
	 * @param pricingSnapshot the pricing snapshot of the shopping cart containing the shopping items
	 * @param currency the currency
	 * @return the subtotal
	 */
	Money calculate(Stream<? extends ShoppingItem> shoppingItems, ShoppingCartPricingSnapshot pricingSnapshot, Currency currency);

	/**
	 * Calculates the subtotal of the shopping items, including item discounts, if present.
	 *
	 * @param shoppingItems the shopping items to evaluate
	 * @param pricingSnapshot the pricing snapshot of the shopping cart containing the shopping items
	 * @param currency the currency
	 * @return the subtotal
	 */
	default Money calculate(Collection<? extends ShoppingItem> shoppingItems, ShoppingCartPricingSnapshot pricingSnapshot, Currency currency) {
		return calculate(shoppingItems.stream(), pricingSnapshot, currency);
	}

	/**
	 * Calculates the subtotal of the shopping items, using the given function to decide what item price to use.
	 *
	 * @param shoppingItems the shopping items to evaluate
	 * @param pricingSnapshot the pricing snapshot of the shopping cart containing the shopping items
	 * @param currency the currency
	 * @param shoppingItemCostFunction the function to extract each {@link ShoppingItem} price to use in the subtotal calculation.
	 * @return the subtotal
	 */
	Money calculate(Stream<? extends ShoppingItem> shoppingItems, ShoppingCartPricingSnapshot pricingSnapshot, Currency currency,
					Function<ShoppingItemPricingSnapshot, Money> shoppingItemCostFunction);

	/**
	 * Calculates the subtotal of the shopping items, using the given function to decide what item price to use.
	 *
	 * @param shoppingItems the shopping items to evaluate
	 * @param pricingSnapshot the pricing snapshot of the shopping cart containing the shopping items
	 * @param currency the currency
	 * @param shoppingItemCostFunction the function to extract each {@link ShoppingItem} price to use in the subtotal calculation.
	 * @return the subtotal
	 */
	default Money calculate(Collection<? extends ShoppingItem> shoppingItems, ShoppingCartPricingSnapshot pricingSnapshot, Currency currency,
					Function<ShoppingItemPricingSnapshot, Money> shoppingItemCostFunction) {
		return calculate(shoppingItems.stream(), pricingSnapshot, currency, shoppingItemCostFunction);
	}

}
