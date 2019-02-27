/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.populators.impl;

import java.math.BigDecimal;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemBuilderPopulator;
import com.elasticpath.shipping.connectivity.dto.impl.ShippableItemImpl;

/**
 * Generic implementation of {@link ShippableItemBuilderPopulator} to build {@link ShippableItem}.
 *
 * @param <I> the interface type of the item being populated, extending {@link ShippableItem}.
 * @param <C> the concrete type of the item being populated, extending {@link ShippableItemImpl}.
 * @param <P> the interface type of this Populator, extending {@link ShippableItemBuilderPopulator}.
 */
public class ShippableItemBuilderPopulatorImpl<I extends ShippableItem, C extends ShippableItemImpl, P extends ShippableItemBuilderPopulator>
		extends AbstractRespawnBuilderPopulatorImpl<I, C, P> implements ShippableItemBuilderPopulator {

	@Override
	protected void copy(final I shippableItem) {
		withSkuGuid(shippableItem.getSkuGuid());
		withQuantity(shippableItem.getQuantity());
		withWeight(shippableItem.getWeight());
		withHeight(shippableItem.getHeight());
		withWidth(shippableItem.getWidth());
		withLength(shippableItem.getLength());
	}

	@Override
	public P withSkuGuid(final String skuGuid) {
		getInstanceUnderBuild().setSkuGuid(skuGuid);
		return self();
	}

	@Override
	public String getSkuGuid() {
		return getInstanceUnderBuild().getSkuGuid();
	}

	@Override
	public P withQuantity(final int quantity) {
		getInstanceUnderBuild().setQuantity(quantity);
		return self();
	}

	@Override
	public int getQuantity() {
		return getInstanceUnderBuild().getQuantity();
	}

	@Override
	public P withWeight(final BigDecimal weight) {
		getInstanceUnderBuild().setWeight(weight);
		return self();
	}

	@Override
	public BigDecimal getWeight() {
		return getInstanceUnderBuild().getWeight();
	}

	@Override
	public P withHeight(final BigDecimal height) {
		getInstanceUnderBuild().setHeight(height);
		return self();
	}

	@Override
	public BigDecimal getHeight() {
		return getInstanceUnderBuild().getHeight();
	}

	@Override
	public P withWidth(final BigDecimal width) {
		getInstanceUnderBuild().setWidth(width);
		return self();
	}

	@Override
	public BigDecimal getWidth() {
		return getInstanceUnderBuild().getWidth();
	}

	@Override
	public P withLength(final BigDecimal length) {
		getInstanceUnderBuild().setLength(length);
		return self();
	}

	@Override
	public BigDecimal getLength() {
		return getInstanceUnderBuild().getLength();
	}
}
