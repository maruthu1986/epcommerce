/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;

/**
 * Shipping cost calculation method that calculates the shipping cost as fixBase + % of order total. It needs two parameters: the value of fixBase
 * and the value of percentage of the order total.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("fixedBaseAndOrderTotalPercentageMethod")
@DataCache(enabled = true)
public class FixedBaseAndOrderTotalPercentageMethodImpl extends AbstractShippingCostCalculationMethodImpl {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/** Make sure the METHOD_TYPE value match the bean id used in the spring bean factory configuration. */
	private static final String METHOD_TYPE = "fixedBaseAndOrderTotalPercentageMethod";

	private static final String METHOD_TEXT = "FixedBaseAndOrderTotalPercentageMethod_method_text";

	/** Set of keys required for this shipping cost calculation method. */
	private static final String[] PARAMETER_KEYS = new String[] { ShippingCostCalculationParametersEnum.FIXED_BASE.getKey(),
			ShippingCostCalculationParametersEnum.PERCENTAGE_OF_ORDER_TOTOAL.getKey() };

	private static final BigDecimal PERCENT_CONVERT = new BigDecimal("100.0");

	/**
	 * Must be implemented by subclasses to return their type. Make sure this matches the hibernate subclass discriminator-value and the spring
	 * context bean id for this RuleAction implementation.
	 * 
	 * @return the kind of the action subclass.
	 */
	@Override
	@Transient
	protected String getMethodType() {
		return METHOD_TYPE;
	}

	/**
	 * Return an array of parameter keys required by this rule action.
	 * 
	 * @return the parameter key array
	 */
	@Override
	@Transient
	public String[] getParameterKeys() {
		return PARAMETER_KEYS.clone();
	}

	/**
	 * Return the text representation of this method for display to the user.
	 * 
	 * @return the text representation
	 */
	@Override
	@Transient
	public String getDisplayText() {
		return METHOD_TEXT;
	}

	@Override
	public Money calculateShippingCost(final Collection<? extends ShippableItem> shippableItems,
									   final Money shippableItemsSubtotal,
									   final Currency currency,
									   final ProductSkuLookup productSkuLookup) {
		if (isEmpty(shippableItems)) {
			return Money.valueOf(BigDecimal.ZERO, currency);
		}
		final BigDecimal fixedBase = new BigDecimal(
				this.getParamValue(ShippingCostCalculationParametersEnum.FIXED_BASE.getKey(), currency));
		//not currency specific ...
		final BigDecimal percentage = new BigDecimal(
				this.getParamValue(ShippingCostCalculationParametersEnum.PERCENTAGE_OF_ORDER_TOTOAL.getKey()));
		return Money.valueOf(fixedBase.
				add(percentage.
						multiply(shippableItemsSubtotal.getAmount()).
						divide(PERCENT_CONVERT, 2, BigDecimal.ROUND_HALF_UP)), currency);
	}
}
