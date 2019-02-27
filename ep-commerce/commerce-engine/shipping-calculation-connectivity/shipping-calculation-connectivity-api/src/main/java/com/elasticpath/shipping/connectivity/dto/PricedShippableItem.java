/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto;

import com.elasticpath.money.Money;

/**
 * Interface defining a shippable item with price.
 */
public interface PricedShippableItem extends ShippableItem {

	/**
	 * Returns the unit price of the item (price per individual quantity).
	 * Note: This price already reflects any promotions applied to this item and also to the whole cart as any cart-level discounts
	 * are apportioned to the item before calculating this unit price.
	 *
	 * @return the unit price to pay (price to pay for one quantity of this item).
	 */
	Money getUnitPrice();

	/**
	 * Returns the total price of the item, which is effectively unit price multiplied by quantity.
	 * Note: This price already reflects any promotions applied to this item and also to the whole cart as any cart-level discounts
	 * are apportioned to the item before calculating this total price.
	 *
	 * @return the total price to pay for all quantity of this item.
	 */
	Money getTotalPrice();

}
