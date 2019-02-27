/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.money.Money;
import com.elasticpath.service.shoppingcart.impl.ItemPricing;

/**
 * Provides a snapshot of derived pricing members pertaining to a {@link com.elasticpath.domain.shoppingcart.ShoppingItem ShoppingItem}.
 */
public interface ShoppingItemPricingSnapshot {

	/**
	 * Get the Price of an item if it has been set.
	 * Note: this will not be guaranteed to return all price tier pricing
	 *
	 * @return the price
	 */
	Price getPrice();

	/**
	 * @return the list unit price as a <code>Money</code> object.
	 */
	Money getListUnitPrice();

	/**
	 * @return the sale unit price as a <code>Money</code> object.  Can be null.
	 */
	Money getSaleUnitPrice();

	/**
	 * @return the catalog promoted price as a <code>Money</code> object.  Can be null.
	 */
	Money getPromotedUnitPrice();

	/**
	 * Gets the {@link com.elasticpath.service.shoppingcart.impl.ItemPricing}.
	 *
	 * @return {@link com.elasticpath.service.shoppingcart.impl.ItemPricing}.
	 */
	ItemPricing getLinePricing();

	/**
	 * Get the discount as a <code>Money</code> object.
	 *
	 * @return the discount that has been applied to this line item.
	 */
	Money getDiscount();

	/**
	 * Get the total amount as a <code>Money</code> object.
	 *
	 * @return the total amount that this line item is worth: (lowest unit price * quantity) - discount.
	 * @deprecated Call getPriceCalc().withCartDiscounts().getMoney() instead.
	 */
	@Deprecated
	Money getTotal();

	/**
	 * Retrieve a PriceCalculator that can be used for determining the appropriate cart item price to return.
	 * @return PriceCalculator object
	 */
	PriceCalculator getPriceCalc();

}
