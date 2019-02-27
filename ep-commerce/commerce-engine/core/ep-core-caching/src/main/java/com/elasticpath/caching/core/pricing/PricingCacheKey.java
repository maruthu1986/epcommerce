/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.caching.core.pricing;

import java.util.Objects;

import com.elasticpath.domain.pricing.PriceListStack;

/**
 * A key used to define a unique pricing. Combines a PriceListStack and ProductSku.
 */
public class PricingCacheKey {

	private final PriceListStack stack;
	private final String skuCode;

	/**
	 * Create a new Pricing Key.
	 * @param stack the price list stack
	 * @param skuCode the product sku's code
	 */
	public PricingCacheKey(final PriceListStack stack, final String skuCode) {
		this.stack = stack;
		this.skuCode = skuCode;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PricingCacheKey)) {
			return false;
		}
		PricingCacheKey that = (PricingCacheKey) other;
		return Objects.equals(stack, that.stack)
			   && Objects.equals(skuCode, that.skuCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stack, skuCode);
	}
}
