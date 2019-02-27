/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.tax;

import java.math.BigDecimal;
import java.util.Map;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;

/**
 * This class wraps up the discount apportioning functionality.  Normal usage is to
 * construct a new instance and call <code>apportionDiscountToShoppingItems</code> and then
 * use <code>getShoppingItemDiscount</code> to retrieve the discount for a particular
 * <code>ShoppingItem</code>.
 */
public interface DiscountApportioningCalculator extends ApportioningCalculator {
	/**
	 * Divide up discount proportionally between the line items. Return a map from shoppingItem unique ordering values to discounts for those
	 * line items.
	 *
	 * Method assumes discount is always less than or equal to the sum of the line item prices.
	 * @param discount the total discount to be applied to the collection of line items
	 * @param shoppingItemPricingSnapshotMap the map of line items and item pricing snapshots in this order to which the discount applies
	 * @return a map from shoppingItem UIDPKs to discounts for those line items.
	 */
	Map<String, BigDecimal> apportionDiscountToShoppingItems(Money discount, Map<? extends ShoppingItem, ShoppingItemPricingSnapshot>
			shoppingItemPricingSnapshotMap);
}
