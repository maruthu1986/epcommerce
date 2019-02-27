/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.visitors;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.builder.populators.BaseShippableItemContainerBuilderPopulator;

/**
 * The base interface for implementing a Populator Visitor to populate a type of
 * {@link com.elasticpath.shipping.connectivity.dto.ShippableItemContainer} when the Visitor is agnostic of the type of
 * {@link com.elasticpath.shipping.connectivity.dto.ShippableItem} it contains.
 *
 * @see com.elasticpath.service.shipping.transformers.impl.BaseShippableItemContainerTransformerImpl for where this interface is used.
 */
public interface BaseShippableItemContainerPopulatorVisitor
		extends ShippableItemContainerPopulatorVisitor<ShippableItem, BaseShippableItemContainerBuilderPopulator> {
}
