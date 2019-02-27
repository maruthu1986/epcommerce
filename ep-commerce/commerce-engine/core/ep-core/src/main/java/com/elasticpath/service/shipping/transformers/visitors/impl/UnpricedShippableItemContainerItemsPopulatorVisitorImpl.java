/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import java.util.Collection;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.transformers.visitors.UnpricedShippableItemContainerPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemContainerBuilderPopulator;

/**
 * Standard visitor implementation to populate a {@link com.elasticpath.shipping.connectivity.dto.ShippableItemContainer}
 * from the given {@link ShippableItem} objects.
 *
 * Used by {@link com.elasticpath.service.shipping.transformers.impl.BaseShippableItemContainerTransformerImpl}.
 */
public class UnpricedShippableItemContainerItemsPopulatorVisitorImpl implements UnpricedShippableItemContainerPopulatorVisitor {

	@Override
	public void accept(final ShoppingCart shoppingCart, final Collection<ShippableItem> shippableItems,
					   final ShippableItemContainerBuilderPopulator<ShippableItem> populator) {
		populator.withShippableItems(shippableItems);
	}
}
