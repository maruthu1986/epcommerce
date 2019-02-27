/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers;

import java.util.function.Function;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;

/**
 * Interface defining adapter from {@link ShoppingCart} to {@link ShippableItemContainer}.
 */
public interface ShippableItemContainerTransformer extends Function<ShoppingCart, ShippableItemContainer<ShippableItem>>  {
}
