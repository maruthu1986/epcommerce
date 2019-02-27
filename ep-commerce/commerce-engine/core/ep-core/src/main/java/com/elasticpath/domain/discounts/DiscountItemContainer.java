/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
/**
 *
 */
package com.elasticpath.domain.discounts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.rules.PromotionRuleExceptions;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * A discount item container where the discounts can be applied to.
 * It also provides helper methods to get cart items and calculate subtotal for discount calculation.
 */
public interface DiscountItemContainer {

	/**
	 * Record rule id that applied discount.
	 * @param ruleId id of applied discount.
	 * @param actionId the action id
	 * @param discountedItem The item that was discounted or null if applied to the subtotal.
	 * @param discountAmount The amount, not percent, of the discount.
	 * @param quantityAppliedTo The item quantity which the discount was applied to
	 */
	void recordRuleApplied(long ruleId, long actionId, ShoppingItem discountedItem, BigDecimal discountAmount, int quantityAppliedTo);

	/**
	 * Sort the cart Items in discount item container from lowest to highest price.
	 * @return list of cart items.
	 */
	List<ShoppingItem> getItemsLowestToHighestPrice();

	/**
	 * Finds the lowest-priced shopping item for a given SKU code, if one exists.
	 *
	 * @param skuCode the code of a SKU that was discounted
	 * @return a shopping item, if a matching exists
	 */
	Optional<ShoppingItem> getLowestPricedShoppingItemForSku(String skuCode);

	/**
	 * Get the discount item container's catalog.
	 * @return catalog.
	 */
	Catalog getCatalog();

	/**
	 * Gets the calculated price amount for the <code>ShoppingItem</code>.
	 *
	 * @param cartItem cartItem holds price, discount and quantity info.
	 *
	 * @return the calculated price amount.
	 */
	BigDecimal getPriceAmount(ShoppingItem cartItem);

	/**
	 * Gets the per-unit, pre-cart promotion price for the {@link ShoppingItem}.
	 *
	 * @param shoppingItem the shopping item to price
	 * @return the calculated per-unit price amount
	 */
	BigDecimal getPrePromotionUnitPriceAmount(ShoppingItem shoppingItem);

	/**
	 * Find the specific sku and create N sku quantities in cart item.
	 * @param skuCode the given skuCode.
	 * @param numItems create N items based on the given sku code.
	 * @return cart items with N quantities. Return null if shopping item is out of stock.
	 * to create a cart item and then add it to the shopping cart.
	 */
	ShoppingItem addCartItem(String skuCode, int numItems);

	/**
	 * Gets the per-unit, pre-cart promotion price for the {@link ShippingOption}.
	 *
	 * @param shippingOption the shipping option to price
	 * @return the calculated price amount
	 */
	BigDecimal getPrePromotionPriceAmount(ShippingOption shippingOption);

	/**
	 * Applies a discount to the shopping cart subtotal.
	 *
	 * @param discountAmount the amount to discount the subtotal by as a BigInteger
	 * @param ruleId the rule which caused the discount
	 * @param actionId the action which caused the discount
	 */
	void applySubtotalDiscount(BigDecimal discountAmount, long ruleId, long actionId);

	/**
	 * Applies a discount to the shipping option.
	 *
	 * @param shippingOption the shipping option that qualifies for a discount
	 * @param ruleId the ID of the rule that caused the discount
	 * @param actionId the ID of the action that caused the discount
	 * @param discountAmount the amount by which to discount the shipping cost
	 */
	void applyShippingOptionDiscount(ShippingOption shippingOption, long ruleId, long actionId, BigDecimal discountAmount);

	/**
	 * Calculates the subtotal of the cart minus the amount of all the items
	 * that may not have discounts applied to them. This is required so that we do not apply
	 * promotions on gift certificates in the cart, for example.
	 *
	 * @return the subtotal
	 */
	BigDecimal calculateSubtotalOfDiscountableItems();

	/**
	 * Calculates the subtotal of the cart minus the amount of all the items
	 * that may not have discounts applied to them. This is required so that we do not apply
	 * promotions on gift certificates in the cart, for example.
	 *
	 * @param promotionRuleExceptions exceptions that should not contribute to the subtotal
	 * @return the subtotal
	 */
	BigDecimal calculateSubtotalOfDiscountableItemsExcluding(PromotionRuleExceptions promotionRuleExceptions);

	/**
	 * Calculates the subtotal of the cart minus the amount of all the items that are not shippable.
	 *
	 * @return the subtotal
	 */
	BigDecimal calculateSubtotalOfShippableItems();

	/**
	 * Checks whether a CartItem help the cart satisfy the promotion conditions, given exceptions to the promotion
	 * rule.
	 *
	 * @param cartItem   the cart item in question
	 * @param exceptions the exclusions to the promotion
	 * @return true if the CartItem is eligible for a promotion with the given exceptions, false if not
	 */
	boolean cartItemEligibleForPromotion(ShoppingItem cartItem, PromotionRuleExceptions exceptions);

	/**
	 * Gets the map of limited usage promotion rule codes to rule ids.
	 *
	 * @return the map of limited usage promotion rule codes to rule ids
	 */
	Map<String, Long> getLimitedUsagePromotionRuleCodes();

}
