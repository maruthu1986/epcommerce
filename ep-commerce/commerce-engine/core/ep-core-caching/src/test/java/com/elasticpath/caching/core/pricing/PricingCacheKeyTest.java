/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.caching.core.pricing;

import java.util.Currency;

import com.google.common.testing.EqualsTester;
import org.junit.Test;

import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.pricing.impl.PriceListStackImpl;

/**
 * Test the validity of the pricing cache key.
 */
public class PricingCacheKeyTest {

	@Test
	public void testEqualsHashCode() {

		PriceListStack stack1 = new PriceListStackImpl();
		stack1.setCurrency(Currency.getInstance("CAD"));
		stack1.addPriceList("PLGUID");

		PriceListStack stack2 = new PriceListStackImpl();
		stack2.setCurrency(Currency.getInstance("USD"));
		stack2.addPriceList("OTHER_PLGUID");

		new EqualsTester()
			.addEqualityGroup(new PricingCacheKey(stack1, "SKUCODE"), new PricingCacheKey(stack1, "SKUCODE"))
			.addEqualityGroup(new PricingCacheKey(stack1, "OTHER_SKUCODE"))
			.addEqualityGroup(new PricingCacheKey(stack2, "SKUCODE"))
			.testEquals();
	}
}