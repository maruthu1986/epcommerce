/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shipping.ShippableItemsPricing;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;

/**
 * Interface defining a transformer from a collection of {@link ShoppingItem} objects to a Stream of {@link PricedShippableItem} objects.
 */
public interface PricedShippableItemsTransformer extends BiFunction<Collection<? extends ShoppingItem>, ShippableItemsPricing,
																	Stream<PricedShippableItem>> {
}
