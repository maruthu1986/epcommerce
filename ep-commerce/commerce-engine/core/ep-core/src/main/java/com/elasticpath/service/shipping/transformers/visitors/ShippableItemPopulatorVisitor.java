/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors;

import java.util.function.BiConsumer;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemBuilderPopulator;

/**
 * The interface for implementing a Populator Visitor to populate a type of {@link com.elasticpath.shipping.connectivity.dto.ShippableItem}.
 *
 * @see com.elasticpath.service.shipping.transformers.impl.ShippableItemTransformerImpl for where this interface is used.
 * @see com.elasticpath.service.shipping.transformers.impl.PricedShippableItemTransformerImpl as it's also used there too.
 */
public interface ShippableItemPopulatorVisitor extends BiConsumer<ShoppingItem, ShippableItemBuilderPopulator> {
}
