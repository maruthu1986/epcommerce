/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers;

import java.util.function.BiFunction;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shipping.ShippableItemPricing;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;

/**
 * Interface defining a transformer from {@link ShoppingItem} to {@link PricedShippableItem}.
 */
public interface PricedShippableItemTransformer extends BiFunction<ShoppingItem, ShippableItemPricing, PricedShippableItem> {
}
