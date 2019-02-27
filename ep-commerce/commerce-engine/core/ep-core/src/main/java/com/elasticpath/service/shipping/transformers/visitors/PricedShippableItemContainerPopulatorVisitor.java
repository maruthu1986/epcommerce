/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.PricedShippableItemContainerBuilderPopulator;

/**
 * The interface for implementing a Populator Visitor to populate a type of
 * {@link com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer}.
 *
 * @see com.elasticpath.service.shipping.transformers.impl.PricedShippableItemContainerTransformerImpl for where this interface is used.
 */
public interface PricedShippableItemContainerPopulatorVisitor
		extends ShippableItemContainerPopulatorVisitor<PricedShippableItem, PricedShippableItemContainerBuilderPopulator<PricedShippableItem>> {
}
