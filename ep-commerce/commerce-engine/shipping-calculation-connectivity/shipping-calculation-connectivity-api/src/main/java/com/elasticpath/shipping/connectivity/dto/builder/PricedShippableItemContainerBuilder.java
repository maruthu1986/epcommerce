/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemContainerBuilderPopulator;

/**
 * Interface defining builder of {@link PricedShippableItemContainer}.
 */
public interface PricedShippableItemContainerBuilder
		extends PricedShippableItemContainerBuilderPopulator<PricedShippableItem>,
		Builder<PricedShippableItemContainer<PricedShippableItem>,
				PricedShippableItemContainerBuilderPopulator<PricedShippableItem>> {
}
