/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shipping.transformers.ShippableItemTransformer;
import com.elasticpath.service.shipping.transformers.visitors.ShippableItemPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.ShippableItemBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemBuilderPopulator;

/**
 * Implementation of {@link ShippableItemTransformer}.
 */
public class ShippableItemTransformerImpl implements ShippableItemTransformer {
	private Supplier<ShippableItemBuilder> supplier;
	private List<ShippableItemPopulatorVisitor> visitors;

	@Override
	public ShippableItem apply(final ShoppingItem shoppingItem) {
		requireNonNull(shoppingItem, "ShoppingItem is required.");

		final ShippableItemBuilder builder = getSupplier().get();
		final ShippableItemBuilderPopulator populator = builder.getPopulator();

		Optional.ofNullable(getVisitors())
				.ifPresent(visitors -> visitors.forEach(consumer -> consumer.accept(shoppingItem, populator)));

		return builder.build();
	}

	protected Supplier<ShippableItemBuilder> getSupplier() {
		return this.supplier;
	}

	public void setSupplier(final Supplier<ShippableItemBuilder> supplier) {
		this.supplier = supplier;
	}

	protected List<ShippableItemPopulatorVisitor> getVisitors() {
		return this.visitors;
	}

	public void setVisitors(final List<ShippableItemPopulatorVisitor> visitors) {
		this.visitors = visitors;
	}
}
