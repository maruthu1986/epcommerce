/*
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.service.order.impl;

import java.io.Serializable;
import java.util.function.Predicate;

import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;

/**
 * A {@link Predicate} that evaluates whether a {@link com.elasticpath.domain.shoppingcart.ShoppingItem ShoppingItem} has recurring charges.
 */
public class ShoppingItemHasRecurringPricePredicate implements Predicate<ShoppingItemPricingSnapshot>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Checks whether the item has any recurring charges.
	 *
	 * @param shoppingItemPricingSnapshot the shopping item pricing snapshot
	 * @return true if the item has recurring price
	 */
	@Override
	public boolean test(final ShoppingItemPricingSnapshot shoppingItemPricingSnapshot) {
		return shoppingItemPricingSnapshot.getPrice() != null
				&& shoppingItemPricingSnapshot.getPrice().getPricingScheme() != null
				&& !shoppingItemPricingSnapshot.getPrice().getPricingScheme().getSchedules(PriceScheduleType.RECURRING).isEmpty();
	}

}
