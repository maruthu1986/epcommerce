/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.impl;

import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.builder.PricedShippableItemContainerBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemContainerBuilderPopulator;
import com.elasticpath.shipping.connectivity.dto.builder.populators.impl.PricedShippableItemContainerBuilderPopulatorImpl;
import com.elasticpath.shipping.connectivity.dto.impl.PricedShippableItemContainerImpl;

/**
 * Implementation of {@link PricedShippableItemContainerBuilder} to build {@link PricedShippableItemContainer}.
 */
public class PricedShippableItemContainerBuilderImpl
		extends PricedShippableItemContainerBuilderPopulatorImpl<PricedShippableItemContainer<PricedShippableItem>,
																 PricedShippableItem,
																 PricedShippableItemContainerImpl<PricedShippableItem>,
																 PricedShippableItemContainerBuilderPopulator<PricedShippableItem>>
		implements PricedShippableItemContainerBuilder {
	@Override
	public PricedShippableItemContainerBuilderPopulator<PricedShippableItem> getPopulator() {
		return this;
	}

	@Override
	protected Optional<Supplier<PricedShippableItemContainerImpl<PricedShippableItem>>> createDefaultInstanceSupplier() {
		return Optional.of(PricedShippableItemContainerImpl::new);
	}
}
