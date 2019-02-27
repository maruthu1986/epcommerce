/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.shipping.impl;

import java.util.Locale;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.money.Money;
import com.elasticpath.service.shipping.ShippingOptionTransformer;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingOptionBuilder;

/**
 * Transformer of {@link ShippingOption}.
 */
public class ShippingOptionTransformerImpl implements ShippingOptionTransformer {
	private Supplier<ShippingOptionBuilder> shippingOptionBuilderSupplier;

	@Override
	public ShippingOption transform(final ShippingServiceLevel shippingServiceLevel,
									final Supplier<Money> shippingCostCalculator,
									final Locale locale) {
		return shippingOptionBuilderSupplier.get()
				.withCode(shippingServiceLevel.getCode())
				.withDisplayNames(ImmutableMap.of(locale, shippingServiceLevel.getDisplayName(locale, true)))
				.withCarrierCode(shippingServiceLevel.getCarrier())
				.withCarrierDisplayName(shippingServiceLevel.getCarrier())
				.withShippingCost(shippingCostCalculator.get())
				.build();
	}

	public void setShippingOptionBuilderSupplier(final Supplier<ShippingOptionBuilder> shippingOptionBuilderSupplier) {
		this.shippingOptionBuilderSupplier = shippingOptionBuilderSupplier;
	}
}
