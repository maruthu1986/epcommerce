/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemBuilderPopulator;

/**
 * Interface defining builder of {@link PricedShippableItem}.
 */
public interface PricedShippableItemBuilder extends PricedShippableItemBuilderPopulator,
		Builder<PricedShippableItem, PricedShippableItemBuilderPopulator> {
}
