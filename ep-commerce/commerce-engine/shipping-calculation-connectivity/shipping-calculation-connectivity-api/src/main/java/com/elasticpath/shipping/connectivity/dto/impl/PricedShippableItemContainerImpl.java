/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.impl;

import java.io.Serializable;

import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;

/**
 * Implements {@link PricedShippableItemContainer}.
 *
 * @param <E> type of {@link PricedShippableItem} that this container contains.
 */
public class PricedShippableItemContainerImpl<E extends PricedShippableItem> extends ShippableItemContainerImpl<E>
		implements PricedShippableItemContainer<E>, Serializable {

	private static final long serialVersionUID = 5000000001L;

}
