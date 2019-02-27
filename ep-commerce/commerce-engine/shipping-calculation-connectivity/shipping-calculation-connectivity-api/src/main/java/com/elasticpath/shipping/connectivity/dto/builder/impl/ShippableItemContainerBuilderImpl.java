/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.impl;

import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.builder.ShippableItemContainerBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemContainerBuilderPopulator;
import com.elasticpath.shipping.connectivity.dto.builder.populators.impl.ShippableItemContainerBuilderPopulatorImpl;
import com.elasticpath.shipping.connectivity.dto.impl.ShippableItemContainerImpl;

/**
 * Implementation of {@link ShippableItemContainerBuilder} to build {@link ShippableItemContainer}.
 */
public class ShippableItemContainerBuilderImpl
		extends ShippableItemContainerBuilderPopulatorImpl<ShippableItemContainer<ShippableItem>,
														   ShippableItem,
														   ShippableItemContainerImpl<ShippableItem>,
														   ShippableItemContainerBuilderPopulator<ShippableItem>>
		implements ShippableItemContainerBuilder {
	@Override
	public ShippableItemContainerBuilderPopulator<ShippableItem> getPopulator() {
		return this;
	}

	@Override
	protected Optional<Supplier<ShippableItemContainerImpl<ShippableItem>>> createDefaultInstanceSupplier() {
		return Optional.of(ShippableItemContainerImpl::new);
	}
}
