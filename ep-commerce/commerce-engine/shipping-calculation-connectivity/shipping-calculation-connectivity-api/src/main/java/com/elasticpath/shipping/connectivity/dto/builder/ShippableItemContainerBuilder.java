/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemContainerBuilderPopulator;

/**
 * Interface defining builder of {@link ShippableItemContainer}.
 */
public interface ShippableItemContainerBuilder
		extends ShippableItemContainerBuilderPopulator<ShippableItem>,
		Builder<ShippableItemContainer<ShippableItem>, ShippableItemContainerBuilderPopulator<ShippableItem>> {
}
