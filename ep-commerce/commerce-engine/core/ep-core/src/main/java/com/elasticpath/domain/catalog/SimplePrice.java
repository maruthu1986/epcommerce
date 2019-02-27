/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.catalog;

import java.util.Collection;
import java.util.Currency;
import java.util.Set;

import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.money.Money;

/**
 * Methods that represent a simple price.
 */
public interface SimplePrice {

	/**
	 * Get the product's list price.
	 *
	 * @param qty of the product
	 * @return the list price as a <code>Money</code>
	 */
	Money getListPrice(int qty);

	/**
	 * Get the product's sale price.
	 *
	 * @param qty the quantity of the product
	 * @return the sale price as a <code>Money</code>
	 */
	Money getSalePrice(int qty);

	/**
	 * Get the product's computed price (e.g. the result of executing a rule).
	 *
	 * @param qty the quantity of the product
	 * @return the computed price as a <code>Money</code> Returns null if no computed price has been set
	 */
	Money getComputedPrice(int qty);

	/**
	 * Get the {@link DiscountRecord}s representing the promotions that were valid for this price, for the given quantity.
	 *
	 * @param qty the quantity of the product
	 * @return the corresponding {@link DiscountRecord}s, or an empty collection if none present; never {@code null}
	 */
	Collection<DiscountRecord> getDiscountRecords(int qty);

	/**
	 * Get the lowest of the price values specified in this <code>Price</code>.
	 *
	 * @param qty the quantity of the product
	 * @return the lowest price as a <code>Money</code>
	 */
	Money getLowestPrice(int qty);

	/**
	 * Get the lowest of the price values specified in this <code>Price</code> for the minimum tier.
	 *
	 * @return the lowest price as a <code>Money</code>
	 */
	Money getLowestPrice();

	/**
	 * Calculates the <code>Money</code> savings if the price has a discount.
	 *
	 * @param qty the quantity of the product
	 * @return the price savings as a <code>Money</code>
	 */
	Money getDollarSavings(int qty);

	/**
	 * Get the pre-promotion price of the corresponding price tier, which promotions are to be applied.
	 * This is currently the lower of the sale price and the list price.
	 *
	 * @param qty the quantity of the product
	 * @return a <code>Money</code> object representing the pre-promotion price
	 */
	Money getPrePromotionPrice(int qty);

	/**
	 * Check if the lowest price is less than the list price, i.e. the price has a discount.
	 *
	 * @param qty the quantity of the product
	 * @return true if the price has a lower price than the list price.
	 */
	boolean isLowestLessThanList(int qty);

	/**
	 * Get the currency for this product price.
	 *
	 * @return the <code>Currency</code>
	 */
	Currency getCurrency();

	/**
	 * Get the minimum quantity of first price tier.
	 *
	 * @return the minimum quantity of first price tier return 1 if no price tier has been set.
	 */
	int getFirstPriceTierMinQty();

	/**
	 * Get the minimum quantities for available price tiers.
	 *
	 * @return set of integers for tier minimum quantities. The iterator on the set will return values in an ascending order.
	 */
	Set<Integer> getPriceTiersMinQuantities();

}
