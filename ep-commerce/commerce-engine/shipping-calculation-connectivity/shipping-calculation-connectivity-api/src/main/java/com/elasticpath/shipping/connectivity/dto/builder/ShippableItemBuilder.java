/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemBuilderPopulator;

/**
 * Interface defining builder of {@link ShippableItem}.
 */
public interface ShippableItemBuilder extends ShippableItemBuilderPopulator,
		Builder<ShippableItem, ShippableItemBuilderPopulator> {
}
