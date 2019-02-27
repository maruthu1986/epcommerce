/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart;

import java.math.BigDecimal;

import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * The ShoppingCartPricingSnapshot holds transient calculated pricing information for
 * a {@link com.elasticpath.domain.shoppingcart.ShoppingCart}.
 */
public interface ShoppingCartPricingSnapshot {

	/**
	 * Get the amount redeemed from gift certificate.
	 *
	 * @return the gift certificate discounted from the total
	 */
	BigDecimal getGiftCertificateDiscount();

	/**
	 * Get the amount redeemed from gift certificate.
	 *
	 * @return the gift certificate discount as a <code>Money</code> object
	 */
	Money getGiftCertificateDiscountMoney();

	/**
	 * Given a ShoppingItem, returns the corresponding {@link ShoppingItemPricingSnapshot}.
	 * If no such snapshot exists, throws an EpServiceException.
	 *
	 * @param item a shopping item
	 * @return the corresponding ShoppingItemPricingSnapshot
	 * @throws com.elasticpath.base.exception.EpServiceException if the corresponding snapshot does not exist
	 */
	ShoppingItemPricingSnapshot getShoppingItemPricingSnapshot(ShoppingItem item);

	/**
	 * Given an {@link ShippingOption}, returns the corresponding {@link ShippingPricingSnapshot}.
	 * If no such snapshot exists, throws an EpServiceException.
	 *
	 * @param shippingOption a shipping option
	 * @return the corresponding ShippingPricingSnapshot
	 * @throws com.elasticpath.base.exception.EpServiceException if the corresponding snapshot does not exist
	 */
	ShippingPricingSnapshot getShippingPricingSnapshot(ShippingOption shippingOption);

	/**
	 * Get the subtotal of all items in the cart, excluding any subtotal discounts.
	 *
	 * @return a <code>BigDecimal</code> object representing the subtotal.
	 */
	BigDecimal getSubtotal();

	/**
	 * Get the subtotal of all items in the cart, excluding any subtotal discounts.
	 *
	 * @return a <code>Money</code> object representing the subtotal
	 */
	Money getSubtotalMoney();

	/**
	 * Get the discount to the shopping cart subtotal.
	 *
	 * @return the amount discounted from the subtotal
	 */
	BigDecimal getSubtotalDiscount();

	/**
	 * Get the amount discounted from the order subtotal.
	 *
	 * @return the order subtotal discount as a <code>Money</code> object
	 */
	Money getSubtotalDiscountMoney();

	/**
	 * Returns true if an order subtotal discount has been applied.
	 *
	 * @return true if an order subtotal discount has been applied
	 */
	boolean hasSubtotalDiscount();

	/**
	 * Return the shippingCost of the <code>ShoppingCart</code>.
	 *
	 * @return the shippingCost of the <code>ShoppingCart</code>
	 */
	Money getShippingCost();

	/**
	 * Return the before-tax shippingCost.
	 *
	 * @return the before-tax shippingCost.
	 */
	Money getBeforeTaxShippingCost();

	/**
	 * Return the before-tax subtotal.
	 *
	 * @return the before-tax subtotal.
	 */
	Money getBeforeTaxSubTotal();

	/**
	 * Return the before-tax total.
	 *
	 * @return the before-tax total.
	 */
	Money getBeforeTaxTotal();

	/**
	 * Return true if the "inclusive" tax calculation method is in use; otherwise false. This is based on the shippingAddress.
	 *
	 * @return true if the "inclusive" tax calculation method is in use; otherwise false.
	 */
	boolean isInclusiveTaxCalculationInUse();

	/**
	 * Returns the applied rule data for the cart and children.
	 *
	 * @return the promotion rule record container
	 */
	PromotionRecordContainer getPromotionRecordContainer();

}
