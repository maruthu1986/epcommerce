/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.impl;

import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.ShippableItemBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemBuilderPopulator;
import com.elasticpath.shipping.connectivity.dto.builder.populators.impl.ShippableItemBuilderPopulatorImpl;
import com.elasticpath.shipping.connectivity.dto.impl.ShippableItemImpl;

/**
 * Implementation of {@link ShippableItemBuilder} to build {@link ShippableItem}.
 */
public class ShippableItemBuilderImpl extends ShippableItemBuilderPopulatorImpl<ShippableItem, ShippableItemImpl, ShippableItemBuilderPopulator>
		implements ShippableItemBuilder {
	@Override
	public ShippableItemBuilderPopulator getPopulator() {
		return this;
	}

	@Override
	protected Optional<Supplier<ShippableItemImpl>> createDefaultInstanceSupplier() {
		return Optional.of(ShippableItemImpl::new);
	}
}
