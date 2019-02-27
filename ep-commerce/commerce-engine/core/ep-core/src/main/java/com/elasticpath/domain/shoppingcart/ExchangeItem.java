/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.money.Money;

/**
 * Represents an item being exchanged.
 */
public interface ExchangeItem extends ShoppingItem {

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
	 * Retrieve a PriceCalculator that can be used for determining the appropriate unit cart item price to return.
	 * @return PriceCalculator object
	 */
	PriceCalculator getPriceCalc();

	/**
	 * Set the price of the exchange item.
	 *
	 * @param quantity - the new quantity
	 * @param price - the new price (contains Currency)
	 */
	@Override
	void setPrice(int quantity, Price price);


}
