/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.Objects;

import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Records the details of a discount applied via a catalog promotion.
 */
public class CatalogItemDiscountRecordImpl extends AbstractDiscountRecordImpl {

	/**
	 * Serial Version ID.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Constructor.
	 *
	 * @param ruleId the ID of the rule that applied this discount
	 * @param actionId the ID of the rule action that applied this discount
	 * @param discountAmount The amount of this discount per discount record
	 */
	public CatalogItemDiscountRecordImpl(final long ruleId, final long actionId, final BigDecimal discountAmount) {
		super(ruleId, actionId, discountAmount);
	}

	@Override
	public int getCouponUsesRequired(final RuleAction action, final ShoppingCart shoppingCart) {
		return 0;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}

		if (other == null || getClass() != other.getClass()) {
			return false;
		}

		final CatalogItemDiscountRecordImpl otherDiscountRecord = (CatalogItemDiscountRecordImpl) other;

		return Objects.equals(getActionId(), otherDiscountRecord.getActionId())
				&& Objects.equals(getRuleId(), otherDiscountRecord.getRuleId())
				&& Objects.equals(getDiscountAmount(), otherDiscountRecord.getDiscountAmount());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getRuleId(), getActionId(), getDiscountAmount());
	}

}
