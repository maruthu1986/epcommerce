/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.populators.impl;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemContainerBuilderPopulator;
import com.elasticpath.shipping.connectivity.dto.impl.PricedShippableItemContainerImpl;

/**
 * Implementation of {@link PricedShippableItemContainerBuilderPopulator}.
 *
 * @param <I> interface type of the instance being built; an instance of {@link PricedShippableItemContainer}.
 * @param <E> interface type of the shippable items contained by the instance being built; extends {@link PricedShippableItem}.
 * @param <C> concrete type of the instance being built; an instance of {@link PricedShippableItemContainerImpl}.
 * @param <P> interface type of this Populator; an instance of {@link PricedShippableItemContainerBuilderPopulator}.
 */
public class PricedShippableItemContainerBuilderPopulatorImpl<I extends PricedShippableItemContainer<E>,
															  E extends PricedShippableItem,
															  C extends PricedShippableItemContainerImpl<E>,
															  P extends PricedShippableItemContainerBuilderPopulator<E>>
		extends ShippableItemContainerBuilderPopulatorImpl<I, E, C, P>
		implements PricedShippableItemContainerBuilderPopulator<E> {
}
