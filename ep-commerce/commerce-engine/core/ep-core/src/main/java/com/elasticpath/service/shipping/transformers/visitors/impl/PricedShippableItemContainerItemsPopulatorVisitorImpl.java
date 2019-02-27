/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import java.util.Collection;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.transformers.visitors.PricedShippableItemContainerPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemContainerBuilderPopulator;

/**
 * Standard visitor implementation to populate a {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer}
 * from the given {@link PricedShippableItem} objects.
 *
 * Used by {@link com.elasticpath.service.shipping.transformers.impl.BaseShippableItemContainerTransformerImpl}.
 */
public class PricedShippableItemContainerItemsPopulatorVisitorImpl implements PricedShippableItemContainerPopulatorVisitor {

	@Override
	public void accept(final ShoppingCart shoppingCart, final Collection<PricedShippableItem> shippableItems,
					   final PricedShippableItemContainerBuilderPopulator<PricedShippableItem> populator) {
		populator.withShippableItems(shippableItems);
	}
}
