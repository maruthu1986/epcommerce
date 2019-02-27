/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.populators;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;

/**
 * Interface defining population methods for building a {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer}.
 *
 * @param <E> interface type of the shippable items contained by the container being built; extending {@link PricedShippableItem}.
 */
public interface PricedShippableItemContainerBuilderPopulator<E extends PricedShippableItem> extends ShippableItemContainerBuilderPopulator<E> {
}
