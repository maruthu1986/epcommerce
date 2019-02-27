/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Represents a discount to a cart subtotal.
 */
public class SubtotalDiscountRecordImpl extends AbstractDiscountRecordImpl {
	
	/** Serial Version ID. **/
	private static final long serialVersionUID = 5000000002L;

	/**
	 * Constructor.
	 *
	 * @param ruleId the ID of the rule that applied this discount
	 * @param actionId the ID of the rule action that applied this discount
	 * @param discountAmount The amount of this discount per discount record
	 */
	public SubtotalDiscountRecordImpl(final long ruleId, final long actionId, final BigDecimal discountAmount) {
		super(ruleId, actionId, discountAmount);
	}

	/**
	 * A coupon is used once for a subtotal discount.
	 * 
	 * @param action The action (ignored).
	 * @param shoppingCart The cart (ignored).
	 * @return The number of coupon uses.
	 */
	@Override
	public int getCouponUsesRequired(final RuleAction action, final ShoppingCart shoppingCart) {
		if (isSuperceded()) {
			return 0;
		}
		return 1;
	}

}
