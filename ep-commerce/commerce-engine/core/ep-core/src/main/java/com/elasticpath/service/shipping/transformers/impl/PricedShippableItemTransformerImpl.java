/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shipping.ShippableItemPricing;
import com.elasticpath.service.shipping.transformers.PricedShippableItemTransformer;
import com.elasticpath.service.shipping.transformers.visitors.PricedShippableItemPopulatorVisitor;
import com.elasticpath.service.shipping.transformers.visitors.ShippableItemPopulatorVisitor;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.PricedShippableItemBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemBuilderPopulator;

/**
 * Implementation of {@link PricedShippableItemTransformer}.
 */
public class PricedShippableItemTransformerImpl implements PricedShippableItemTransformer {

	private Supplier<PricedShippableItemBuilder> supplier;
	private List<ShippableItemPopulatorVisitor> unpricedVisitors;
	private List<PricedShippableItemPopulatorVisitor> pricedVisitors;

	@Override
	public PricedShippableItem apply(final ShoppingItem shoppingItem, final ShippableItemPricing shippableItemPricing) {
		requireNonNull(shoppingItem, "ShoppingItem is required.");
		requireNonNull(shippableItemPricing, "No pricing provided for ShoppingItem");

		final PricedShippableItemBuilder builder = getSupplier().get();
		final PricedShippableItemBuilderPopulator populator = builder.getPopulator();

		Optional.ofNullable(getUnpricedVisitors())
				.ifPresent(visitors -> visitors.forEach(consumer -> consumer.accept(shoppingItem, populator)));

		Optional.ofNullable(getPricedVisitors())
				.ifPresent(visitors -> visitors.forEach(consumer -> consumer.accept(shoppingItem, shippableItemPricing, populator)));

		return builder.build();
	}

	protected Supplier<PricedShippableItemBuilder> getSupplier() {
		return this.supplier;
	}

	public void setSupplier(final Supplier<PricedShippableItemBuilder> supplier) {
		this.supplier = supplier;
	}

	protected List<ShippableItemPopulatorVisitor> getUnpricedVisitors() {
		return this.unpricedVisitors;
	}

	public void setUnpricedVisitors(final List<ShippableItemPopulatorVisitor> unpricedVisitors) {
		this.unpricedVisitors = unpricedVisitors;
	}

	protected List<PricedShippableItemPopulatorVisitor> getPricedVisitors() {
		return this.pricedVisitors;
	}

	public void setPricedVisitors(final List<PricedShippableItemPopulatorVisitor> pricedVisitors) {
		this.pricedVisitors = pricedVisitors;
	}
}
