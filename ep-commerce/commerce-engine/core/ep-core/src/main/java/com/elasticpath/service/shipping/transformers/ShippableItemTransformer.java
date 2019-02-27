/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers;

import java.util.function.Function;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;

/**
 * Interface defining adapter from {@link ShoppingItem} to {@link ShippableItem}.
 */
public interface ShippableItemTransformer extends Function<ShoppingItem, ShippableItem> {
}
