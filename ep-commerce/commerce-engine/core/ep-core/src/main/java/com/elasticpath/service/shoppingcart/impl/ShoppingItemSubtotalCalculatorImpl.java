/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.service.shoppingcart.impl;

import java.util.Currency;
import java.util.function.Function;
import java.util.stream.Stream;

import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shoppingcart.ShoppingItemSubtotalCalculator;
import com.elasticpath.service.shoppingcart.ShoppingItemToPricingSnapshotFunction;

/**
 * Default implementation of {@link ShoppingItemSubtotalCalculator}.
 */
public class ShoppingItemSubtotalCalculatorImpl implements ShoppingItemSubtotalCalculator {

	/**
	 * Calculates the subtotal of the shopping items, including item discounts, if present.
	 *
	 * @param shoppingItems   the shopping items to evaluate
	 * @param pricingSnapshot the pricing snapshot of the shopping cart containing the shopping items
	 * @param currency        the currency
	 * @return the subtotal
	 */
	@Override
	public Money calculate(final Stream<? extends ShoppingItem> shoppingItems, final ShoppingCartPricingSnapshot pricingSnapshot,
			final Currency currency) {
		return calculate(shoppingItems, pricingSnapshot, currency,
				shoppingItemPricingSnapshot -> shoppingItemPricingSnapshot.getPriceCalc().withCartDiscounts().getMoney());
	}

	/**
	 * Calculates the subtotal of the shopping items, using the given function to decide what item price to use.
	 *
	 * @param shoppingItems            the shopping items to evaluate
	 * @param pricingSnapshot          the pricing snapshot of the shopping cart containing the shopping items
	 * @param currency                 the currency
	 * @param shoppingItemCostFunction the function to extract each {@link ShoppingItem} price to use in the subtotal calculation.
	 * @return the subtotal
	 */
	@Override
	public Money calculate(final Stream<? extends ShoppingItem> shoppingItems, final ShoppingCartPricingSnapshot pricingSnapshot,
			final Currency currency, final Function<ShoppingItemPricingSnapshot, Money> shoppingItemCostFunction) {
		return shoppingItems
				.map(createShoppingItemToPricingSnapshotFunction(pricingSnapshot))
				.map(shoppingItemCostFunction)
				.reduce(Money.zero(currency), (currentAmount, amountToAdd) -> currentAmount.add(amountToAdd));
	}

	/**
	 * Creates shopping item to pricing snapshot function from given {@link ShoppingCartPricingSnapshot}.
	 *
	 * @param pricingSnapshot shopping cart pricing snapshot
	 * @return new instance of {@link ShoppingItemToPricingSnapshotFunction}
	 */
	protected Function<ShoppingItem, ShoppingItemPricingSnapshot> createShoppingItemToPricingSnapshotFunction(
			final ShoppingCartPricingSnapshot pricingSnapshot) {
		return new ShoppingItemToPricingSnapshotFunction(pricingSnapshot);
	}

}
