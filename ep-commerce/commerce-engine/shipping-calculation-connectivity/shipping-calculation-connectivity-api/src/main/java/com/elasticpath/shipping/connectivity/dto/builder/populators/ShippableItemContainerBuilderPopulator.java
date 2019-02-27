/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.populators;

import java.util.Collection;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;

/**
 * Interface defining population methods for building a {@link com.elasticpath.shipping.connectivity.dto.ShippableItemContainer}.
 *
 * @param <E> interface type of the shippable items contained by the container being built; extending {@link ShippableItem}.
 */
public interface ShippableItemContainerBuilderPopulator<E extends ShippableItem> extends BaseShippableItemContainerBuilderPopulator {

	/**
	 * Sets the shippable items.
	 *
	 * @param shippableItems the shippable items.
	 * @return this populator.
	 */
	ShippableItemContainerBuilderPopulator<E> withShippableItems(Collection<E> shippableItems);

	/**
	 * @return currently populated shippable items.
	 */
	Collection<E> getShippableItems();
}
