/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors;

import java.util.Collection;

import com.elasticpath.base.function.TriConsumer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;

/**
 * The base interface for implementing a Populator Visitor to populate a type of
 * {@link com.elasticpath.shipping.connectivity.dto.ShippableItemContainer}.
 *
 * @param <E> the interface type of the {@link ShippableItem} the populated container contains.
 * @param <P> the type of Populator being visited.
 * @see com.elasticpath.service.shipping.transformers.impl.BaseShippableItemContainerTransformerImpl for where this interface is used.
 */
public interface ShippableItemContainerPopulatorVisitor<E extends ShippableItem, P>
		extends TriConsumer<ShoppingCart, Collection<E>, P> {
}
