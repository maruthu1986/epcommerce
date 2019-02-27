/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Represents a discount to the shipping amount.
 */
public class ShippingDiscountRecordImpl extends AbstractDiscountRecordImpl {

	/**
	 * Serial Version ID.
	 **/
	private static final long serialVersionUID = 5000000002L;

	private final String shippingOptionCode;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionCode the code of the shipping option to which the discount applies
	 * @param ruleId             the ID of the rule that applied this discount
	 * @param actionId           the ID of the rule action that applied this discount
	 * @param discountAmount     The amount of this discount per discount record
	 */
	public ShippingDiscountRecordImpl(final String shippingOptionCode,
			final long ruleId,
			final long actionId,
			final BigDecimal discountAmount) {
		super(ruleId, actionId, discountAmount);
		this.shippingOptionCode = shippingOptionCode;
	}

	/**
	 * A coupon is used once for a shipping discount.
	 *
	 * @param action       The action (ignored).
	 * @param shoppingCart the cart.
	 * @return The number of coupon uses.
	 */
	@Override
	public int getCouponUsesRequired(final RuleAction action, final ShoppingCart shoppingCart) {
		if (isSuperceded()) {
			return 0;
		}
		final String shippingOptionCodeParameter = action.getParamValue(RuleParameter.SHIPPING_OPTION_CODE_KEY);
		final Optional<ShippingOption> optionalSelectedShippingOption = shoppingCart.getSelectedShippingOption();
		if (optionalSelectedShippingOption.isPresent()
				&& !optionalSelectedShippingOption.get().getCode().equals(shippingOptionCodeParameter)) {
			return 0;
		}
		return 1;
	}

	public String getShippingOptionCode() {
		return shippingOptionCode;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof ShippingDiscountRecordImpl)) {
			return false;
		}

		final ShippingDiscountRecordImpl otherShippingDiscountRecord = (ShippingDiscountRecordImpl) other;

		return Objects.equals(getRuleId(), otherShippingDiscountRecord.getRuleId())
				&& Objects.equals(getActionId(), otherShippingDiscountRecord.getActionId())
				&& Objects.equals(getDiscountAmount(), otherShippingDiscountRecord.getDiscountAmount())
				&& Objects.equals(getShippingOptionCode(), otherShippingDiscountRecord.getShippingOptionCode());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getRuleId(), getActionId(), getDiscountAmount(), getShippingOptionCode());
	}

}
