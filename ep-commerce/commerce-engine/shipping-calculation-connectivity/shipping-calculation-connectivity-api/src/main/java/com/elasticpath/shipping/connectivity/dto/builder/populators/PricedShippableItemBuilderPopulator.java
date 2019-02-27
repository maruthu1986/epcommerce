/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.populators;

import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;

/**
 * Interface defining population methods for building a {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItem}.
 */
public interface PricedShippableItemBuilderPopulator extends ShippableItemBuilderPopulator {

	/**
	 * Populates this Populator with the fields set on the given {@link ShippableItem}.
	 *
	 * @param shippableItem the template {@link ShippableItem} to copy from.
	 * @return this populator.
	 */
	PricedShippableItemBuilderPopulator from(ShippableItem shippableItem);

	/**
	 * Sets the unit price of the item, (price per individual quantity).
	 *
	 * @param unitPrice the unit item price.
	 * @return this populator.
	 * @see com.elasticpath.shipping.connectivity.dto.PricedShippableItem#getUnitPrice() for more information.
	 */
	PricedShippableItemBuilderPopulator withUnitPrice(Money unitPrice);

	/**
	 * @return currently populated item unit price.
	 * @see com.elasticpath.shipping.connectivity.dto.PricedShippableItem#getUnitPrice() for more information.
	 */
	Money getUnitPrice();

	/**
	 * Sets the total price of the item, which is effectively unit price multiplied by quantity.
	 *
	 * @param totalPrice the total item price.
	 * @return this populator.
	 * @see com.elasticpath.shipping.connectivity.dto.PricedShippableItem#getTotalPrice() for more information.
	 */
	PricedShippableItemBuilderPopulator withTotalPrice(Money totalPrice);

	/**
	 * @return currently populated item total price.
	 * @see com.elasticpath.shipping.connectivity.dto.PricedShippableItem#getTotalPrice() for more information.
	 */
	Money getTotalPrice();
}
