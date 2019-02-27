/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors;

import com.elasticpath.base.function.TriConsumer;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shipping.ShippableItemPricing;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemBuilderPopulator;

/**
 * The interface for implementing a Populator Visitor to populate a type of {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItem}.
 *
 * @see com.elasticpath.service.shipping.transformers.impl.PricedShippableItemTransformerImpl for where this interface is used.
 */
public interface PricedShippableItemPopulatorVisitor
		extends TriConsumer<ShoppingItem, ShippableItemPricing, PricedShippableItemBuilderPopulator> {
}
