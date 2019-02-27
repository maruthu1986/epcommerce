/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shipping.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Currency;

import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;

/**
 * Test assertions for shipping cost calculations. 
 */
public final class ShippingAsserts {

	private ShippingAsserts() {
		// Prevent external instantiations
	}
	
	/**
	 * Assert that the shipping cost is as expected.
	 * @param expectedCost the expected shipping cost.
	 * @param method the method to ask to calculate the shipping cost.
	 * @param shippableItemContainer the cart to calculate the shipping cost for.
	 * @param shippableItemsSubtotal the subtotal of the shippable items
	 * @param currency the currency
	 * @param productSkuLookup a sku lookup
	 */
	public static void assertShippingCost(
			final String expectedCost, final ShippingCostCalculationMethod method,
			final ShippableItemContainer<?> shippableItemContainer, final Money shippableItemsSubtotal,
			final Currency currency, final ProductSkuLookup productSkuLookup) {
		assertThat(method.calculateShippingCost(shippableItemContainer.getShippableItems(), shippableItemsSubtotal, currency, productSkuLookup)
				.getAmount())
			.isEqualTo(new BigDecimal(expectedCost));
	}
	
}
