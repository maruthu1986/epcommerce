/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.impl;

import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippableItemPricing;

/**
 * Default implementation of {@link ShippableItemPricing}.
 */
public class ShippableItemPricingImpl implements ShippableItemPricing {
	private final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot;
	private final Money apportionedItemSubtotalUnitDiscount;
	private final Money apportionedItemSubtotalDiscount;

	/**
	 * Constructor.
	 *
	 * @param shoppingItemPricingSnapshot the corresponding pricing information for this item.
	 * @param apportionedItemSubtotalUnitDiscount the apportioned subtotal discount per unit for this particular item.
	 * @param apportionedItemSubtotalDiscount the apportioned subtotal discount for all quantity of this particular item.
	 */
	public ShippableItemPricingImpl(final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot, final Money apportionedItemSubtotalUnitDiscount,
									final Money apportionedItemSubtotalDiscount) {
		this.shoppingItemPricingSnapshot = shoppingItemPricingSnapshot;
		this.apportionedItemSubtotalUnitDiscount = apportionedItemSubtotalUnitDiscount;
		this.apportionedItemSubtotalDiscount = apportionedItemSubtotalDiscount;
	}

	@Override
	public ShoppingItemPricingSnapshot getShoppingItemPricingSnapshot() {
		return this.shoppingItemPricingSnapshot;
	}

	@Override
	public Money getApportionedItemSubtotalUnitDiscount() {
		return this.apportionedItemSubtotalUnitDiscount;
	}

	@Override
	public Money getApportionedItemSubtotalDiscount() {
		return this.apportionedItemSubtotalDiscount;
	}
}
