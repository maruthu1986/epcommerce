/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import static com.elasticpath.commons.constants.MetaDataConstants.SHOPPING_CART_KEY;

import java.util.Collection;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.transformers.visitors.BaseShippableItemContainerPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.BaseShippableItemContainerBuilderPopulator;

/**
 * Implements a Visitor of {@link com.elasticpath.shipping.connectivity.dto.impl.ShippableItemContainerImpl} which populates its fields.
 */
public class ShippableItemContainerMetadataPopulatorVisitorImpl implements BaseShippableItemContainerPopulatorVisitor {

	@Override
	public void accept(final ShoppingCart shoppingCart, final Collection<ShippableItem> shippableItems,
					   final BaseShippableItemContainerBuilderPopulator populator) {
		populator.withField(SHOPPING_CART_KEY, shoppingCart);
	}
}
