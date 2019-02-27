/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.populators.impl;

import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemBuilderPopulator;
import com.elasticpath.shipping.connectivity.dto.impl.PricedShippableItemImpl;

/**
 * Implementation of {@link PricedShippableItemBuilderPopulator} to populate a {@link PricedShippableItem}.
 *
 * @param <I> the interface type of the item being populated, extending {@link PricedShippableItem}.
 * @param <C> the concrete type of the item being populated, extending {@link PricedShippableItemImpl}.
 * @param <P> the interface type of this Populator, extending {@link PricedShippableItemBuilderPopulator}.
 */
public class PricedShippableItemBuilderPopulatorImpl<I extends PricedShippableItem,
													 C extends PricedShippableItemImpl,
													 P extends PricedShippableItemBuilderPopulator>
		extends ShippableItemBuilderPopulatorImpl<I, C, P> implements PricedShippableItemBuilderPopulator {

	@Override
	protected void copy(final I pricedShippableItem) {
		super.copy(pricedShippableItem);

		withUnitPrice(pricedShippableItem.getUnitPrice());
		withTotalPrice(pricedShippableItem.getTotalPrice());
	}

	@Override
	public P withUnitPrice(final Money price) {
		getInstanceUnderBuild().setUnitPrice(price);
		return self();
	}

	@Override
	public Money getUnitPrice() {
		return getInstanceUnderBuild().getUnitPrice();
	}

	@Override
	public P withTotalPrice(final Money price) {
		getInstanceUnderBuild().setTotalPrice(price);
		return self();
	}

	@Override
	public Money getTotalPrice() {
		return getInstanceUnderBuild().getTotalPrice();
	}

	@Override
	@SuppressWarnings("unchecked")
	public P from(final ShippableItem shippableItem) {
		// Just call super method to copy the ShippableItem fields only
		super.copy((I) shippableItem);

		return self();
	}
}
