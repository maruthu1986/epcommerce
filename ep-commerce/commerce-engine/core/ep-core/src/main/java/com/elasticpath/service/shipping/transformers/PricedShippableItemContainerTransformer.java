/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers;

import java.util.function.BiFunction;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;

/**
 * Interface defining adapter from {@link ShoppingCart} to {@link PricedShippableItemContainer}.
 */
public interface PricedShippableItemContainerTransformer
		extends BiFunction<ShoppingCart, ShoppingCartPricingSnapshot, PricedShippableItemContainer<PricedShippableItem>> {
}
