/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippableItemPricing;
import com.elasticpath.service.shipping.transformers.visitors.PricedShippableItemPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemBuilderPopulator;

/**
 * Standard visitor implementationn to populate a {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItem}
 * from a {@link ShoppingItem} and its corresponding {@link com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot}.
 *
 * Used by {@link com.elasticpath.service.shipping.transformers.impl.PricedShippableItemTransformerImpl}.
 */
public class PricedShippableItemPopulatorVisitorImpl implements PricedShippableItemPopulatorVisitor {
	@Override
	public void accept(final ShoppingItem shoppingItem, final ShippableItemPricing shippableItemPricing,
					   final PricedShippableItemBuilderPopulator populator) {
		requireNonNull(shippableItemPricing, "No pricing provided for ShoppingItem");

		populator.withUnitPrice(getItemUnitPrice(shippableItemPricing))
				.withTotalPrice(getItemTotalPrice(shippableItemPricing));
	}

	/**
	 * Gets the item unit price after all item level and cart-level discounts have been applied.
	 *
	 * @param shippableItemPricing the item pricing to use to get the item unit price after discount.
	 * @return the item unit price after all item level and cart-level discounts have been applied.
	 */
	protected Money getItemUnitPrice(final ShippableItemPricing shippableItemPricing) {
		return getDiscountedPrice(shippableItemPricing,
								  pricing -> pricing.getShoppingItemPricingSnapshot().getPriceCalc().forUnitPrice().withCartDiscounts().getMoney(),
								  ShippableItemPricing::getApportionedItemSubtotalUnitDiscount);
	}

	/**
	 * Gets the item total price (ie price all all quantity of this item) after all item level and cart-level discounts have been applied.
	 *
	 * @param shippableItemPricing the item pricing to use to get the item total price after discount.
	 * @return the item total price after all item level and cart-level discounts have been applied.
	 */
	protected Money getItemTotalPrice(final ShippableItemPricing shippableItemPricing) {
		return getDiscountedPrice(shippableItemPricing,
								  pricing -> pricing.getShoppingItemPricingSnapshot().getPriceCalc().withCartDiscounts().getMoney(),
								  ShippableItemPricing::getApportionedItemSubtotalDiscount);
	}

	/**
	 * Return the discounted price after all item level and cart-level discounts have been applied.
	 *
	 * @param itemPricing the item pricing to use to get the price after discount.
	 * @param preSubtotalDiscountFunction {@link Function} to get the price before the apportioned subtotal discount has been applied.
	 * @param apportionedSubtotalDiscountFunction {@link Function} to get the corresponding apportioned subtotal discount for this item.
	 * @return the discounted price after all item level and cart-level discounts have been applied.
	 */
	protected Money getDiscountedPrice(final ShippableItemPricing itemPricing,
									   final Function<ShippableItemPricing, Money> preSubtotalDiscountFunction,
									   final Function<ShippableItemPricing, Money> apportionedSubtotalDiscountFunction) {
		Money price = preSubtotalDiscountFunction.apply(itemPricing);
		final Money discount = apportionedSubtotalDiscountFunction.apply(itemPricing);

		if (discount != null) {
			price = price.subtract(discount);
		}

		return price;
	}
}
