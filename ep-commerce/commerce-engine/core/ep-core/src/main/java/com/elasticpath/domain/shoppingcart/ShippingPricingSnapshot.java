/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart;

import java.io.Serializable;

import com.elasticpath.money.Money;

/**
 * The ShippingPricingSnapshot holds transient calculated pricing information representing the cost of a particular shipping option.
 */
public interface ShippingPricingSnapshot extends Serializable {

	/**
	 * Returns the shipping list price, prior to promotions.
	 *
	 * @return the shipping list price
	 */
	Money getShippingListPrice();

	/**
	 * Returns the shipping promoted price.  If no discounts have applied, this will be equal to {@link #getShippingListPrice()}.
	 *
	 * @return the shipping list price
	 */
	Money getShippingPromotedPrice();

	/**
	 * Returns the discount amount applied as a result of promotions.  If no discounts have applied, this will return a non-{@code null}
	 * {@link Money} instance with a value of zero.
	 *
	 * @return the discount amount applied as a result of promotions
	 */
	Money getShippingDiscountAmount();

}
