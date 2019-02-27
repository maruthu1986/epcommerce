/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.Currency;
import java.util.function.Function;
import java.util.stream.Stream;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.service.shoppingcart.OrderSkuSubtotalCalculator;

/**
 * Calculates order sku subtotal.
 */
public class OrderSkuSubtotalCalculatorImpl implements OrderSkuSubtotalCalculator {

	private Function<OrderSku, ShoppingItemPricingSnapshot> orderSkuToPricingSnapshotFunction;

	@Override
	public Money calculate(final Stream<OrderSku> orderSkus, final Currency currency) {
		return calculate(orderSkus, currency,
				shoppingItemPricingSnapshot -> shoppingItemPricingSnapshot.getPriceCalc().withCartDiscounts().getMoney());
	}

	@Override
	public Money calculate(final Stream<OrderSku> orderSkus, final Currency currency,
						   final Function<ShoppingItemPricingSnapshot, Money> orderSkuCostFunction) {
		return orderSkus
				.map(getOrderSkuToPricingSnapshotFunction())
				.map(orderSkuCostFunction)
				.reduce(Money.zero(currency), (currentAmount, amountToAdd) -> currentAmount.add(amountToAdd));
	}

	protected Function<OrderSku, ShoppingItemPricingSnapshot> getOrderSkuToPricingSnapshotFunction() {
		return this.orderSkuToPricingSnapshotFunction;
	}

	public void setOrderSkuToPricingSnapshotFunction(final Function<OrderSku, ShoppingItemPricingSnapshot> orderSkuToPricingSnapshotFunction) {
		this.orderSkuToPricingSnapshotFunction = orderSkuToPricingSnapshotFunction;
	}
}
