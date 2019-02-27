/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors.impl;

import java.util.Collection;
import java.util.Optional;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.transformers.ShippingAddressTransformer;
import com.elasticpath.service.shipping.transformers.visitors.BaseShippableItemContainerPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.builder.populators.BaseShippableItemContainerBuilderPopulator;

/**
 * Standard visitor implementation to populate a {@link com.elasticpath.shipping.connectivity.dto.ShippableItemContainer}
 * from a {@link ShoppingCart} and its items.
 *
 * Used by {@link com.elasticpath.service.shipping.transformers.impl.ShippableItemContainerTransformerImpl}.
 */
public class ShippableItemContainerPopulatorVisitorImpl implements BaseShippableItemContainerPopulatorVisitor {
	private ShippingAddressTransformer shippingAddressTransformer;

	@Override
	public void accept(final ShoppingCart shoppingCart, final Collection<ShippableItem> shippableItems,
					   final BaseShippableItemContainerBuilderPopulator populator) {
		final Shopper shopper = shoppingCart.getShopper();

		final ShippingAddress shippingAddress = Optional.ofNullable(shoppingCart.getShippingAddress())
				.map(getShippingAddressTransformer())
				.orElse(null);

		populator.withDestinationAddress(shippingAddress)
				.withStoreCode(shoppingCart.getStore().getCode())
				.withLocale(shopper.getLocale())
				.withCurrency(shopper.getCurrency());
	}

	protected ShippingAddressTransformer getShippingAddressTransformer() {
		return this.shippingAddressTransformer;
	}

	public void setShippingAddressTransformer(final ShippingAddressTransformer shippingAddressTransformer) {
		this.shippingAddressTransformer = shippingAddressTransformer;
	}
}
