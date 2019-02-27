/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers;

import java.util.function.Function;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;

/**
 * Interface defining a transformer for converting from an {@link PhysicalOrderShipment} to a {@link PricedShippableItemContainer} object.
 *
 * @param <E> the type of {@link PricedShippableItem} that the returned transformed {@link PricedShippableItemContainer} contains.
 */
public interface PricedShippableItemContainerFromOrderShipmentTransformer<E extends PricedShippableItem>
		extends Function<PhysicalOrderShipment, PricedShippableItemContainer<E>> {
}
