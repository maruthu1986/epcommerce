/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart;

import java.util.function.Function;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;

/**
 * <p>{@link Function} to convert from a {@link OrderSku} to a {@link ShoppingItemPricingSnapshot}, with use of a {@link PricingSnapshotService}.</p>
 * <p>This is useful when converting a collection of {@link OrderSku}s and a {@link PricingSnapshotService} to a
 * <code>Map&lt;ShoppingItem, ShoppingItemPricingSnapshot&gt;</code>.</p>
 * <p>Sample usage:
 * <pre>
 *     PricingSnapshotService pricingSnapshotService = ...;
 *     Collection&lt;OrderSku&gt; orderSkus = ...;
 *
 *     Map&lt;OrderSku, ShoppingItemPricingSnapshot&gt; itemPricingSnapshotMap = orderSkus.stream()
 *             .collect(toMap(identity(),
 *                            new OrderSkuToPricingSnapshotFunction(pricingSnapshotService)));
 * </pre>
 * </p>
 */
public class OrderSkuToPricingSnapshotFunction implements Function<OrderSku, ShoppingItemPricingSnapshot> {

	private final PricingSnapshotService pricingSnapshotService;

	/**
	 * Constructor.
	 *
	 * @param pricingSnapshotService the pricing snapshot service
	 */
	public OrderSkuToPricingSnapshotFunction(final PricingSnapshotService pricingSnapshotService) {
		this.pricingSnapshotService = pricingSnapshotService;
	}

	@Override
	public ShoppingItemPricingSnapshot apply(final OrderSku input) {
		return pricingSnapshotService.getPricingSnapshotForOrderSku(input);
	}

}
