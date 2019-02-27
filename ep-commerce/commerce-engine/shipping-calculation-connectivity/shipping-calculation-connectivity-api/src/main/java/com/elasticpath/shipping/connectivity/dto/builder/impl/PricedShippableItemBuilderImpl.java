/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.impl;

import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.PricedShippableItemBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemBuilderPopulator;
import com.elasticpath.shipping.connectivity.dto.builder.populators.impl.PricedShippableItemBuilderPopulatorImpl;
import com.elasticpath.shipping.connectivity.dto.impl.PricedShippableItemImpl;

/**
 * Implementation of {@link PricedShippableItemBuilder} to build {@link PricedShippableItem}.
 */
public class PricedShippableItemBuilderImpl
		extends PricedShippableItemBuilderPopulatorImpl<PricedShippableItem, PricedShippableItemImpl, PricedShippableItemBuilderPopulator>
		implements PricedShippableItemBuilder {
	@Override
	public PricedShippableItemBuilderPopulator getPopulator() {
		return this;
	}

	@Override
	protected Optional<Supplier<PricedShippableItemImpl>> createDefaultInstanceSupplier() {
		return Optional.of(PricedShippableItemImpl::new);
	}
}
