/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.shoppingcart;

import java.util.Collection;
import java.util.Currency;
import java.util.function.Function;
import java.util.stream.Stream;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;

/**
 * Calculates the subtotal of {@link OrderSku} objects.
 * Excludes tax for tax-exclusive regions. Includes tax in tax-inclusive regions.
 */
public interface OrderSkuSubtotalCalculator {

	/**
	 * Calculates the subtotal of the order SKUs, including item discounts, if present.
	 *
	 * @param orderSkus the order skus to evaluate
	 * @param currency the currency
	 * @return the subtotal
	 */
	Money calculate(Stream<OrderSku> orderSkus, Currency currency);

	/**
	 * Calculates the subtotal of the order SKUs, including item discounts, if present.
	 *
	 * @param orderSkus the order skus to evaluate
	 * @param currency the currency
	 * @return the subtotal
	 */
	default Money calculate(Collection<OrderSku> orderSkus, Currency currency) {
		return calculate(orderSkus.stream(), currency);
	}

	/**
	 * Calculates the subtotal of the order SKUs, using the given function to decide what item price to use.
	 *
	 * @param orderSkus the order skus to evaluate
	 * @param currency the currency
	 * @param orderSkuCostFunction the function to extract each {@link OrderSku}'s price to use in the subtotal calculation.
	 * @return the subtotal
	 */
	Money calculate(Stream<OrderSku> orderSkus, Currency currency, Function<ShoppingItemPricingSnapshot, Money> orderSkuCostFunction);

	/**
	 * Calculates the subtotal of the order SKUs, using the given function to decide what item price to use.
	 *
	 * @param orderSkus the order skus to evaluate
	 * @param currency the currency
	 * @param orderSkuCostFunction the function to extract each {@link OrderSku}'s price to use in the subtotal calculation.
	 * @return the subtotal
	 */
	default Money calculate(Collection<OrderSku> orderSkus, Currency currency, Function<ShoppingItemPricingSnapshot, Money> orderSkuCostFunction) {
		return calculate(orderSkus.stream(), currency, orderSkuCostFunction);
	}

}
