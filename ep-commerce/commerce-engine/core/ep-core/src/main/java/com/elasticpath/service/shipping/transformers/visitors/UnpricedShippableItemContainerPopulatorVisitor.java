/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemContainerBuilderPopulator;

/**
 * The interface for implementing a Populator Visitor to populate a type of
 * {@link com.elasticpath.shipping.connectivity.dto.ShippableItemContainer}.
 *
 * @see com.elasticpath.service.shipping.transformers.impl.ShippableItemContainerTransformerImpl for where this interface is used.
 */
public interface UnpricedShippableItemContainerPopulatorVisitor
		extends ShippableItemContainerPopulatorVisitor<ShippableItem, ShippableItemContainerBuilderPopulator<ShippableItem>> {
}
