/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;

/**
 * A {@link BiFunction} which transforms a {@link ShoppingCart} and stream of already transformed {@link ShippableItem} objects and
 * returns a constructed {@link ShippableItemContainer}.
 *
 * @param <I> the interface type of {@link ShippableItemContainer} to return.
 * @param <E> the interface type of elements being passed to this function and that the resultant {@link ShippableItemContainer} should contain.
 */
public interface BaseShippableItemContainerTransformer<I extends ShippableItemContainer<E>, E extends ShippableItem>
		extends BiFunction<ShoppingCart, Stream<E>, I> {
}
