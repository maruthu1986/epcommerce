/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.shipping;

import java.util.Locale;
import java.util.function.Supplier;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Interface of Transforming {@link ShippingServiceLevel} to {@link ShippingOption}.
 */
@FunctionalInterface
public interface ShippingOptionTransformer {

	/**
	 * Transforms a {@link ShippingServiceLevel}, {@link Locale} into {@link ShippingOption}.
	 *
	 * @param shippingServiceLevel shipping service level
	 * @param costCalculator       shipping cost calculator
	 * @param locale               locale
	 * @return unpriced shipping option
	 */
	ShippingOption transform(ShippingServiceLevel shippingServiceLevel, Supplier<Money> costCalculator, Locale locale);

}
