/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto;

/**
 * Interface defining {@link ShippableItemContainer} holds {@link PricedShippableItem}.
 *
 * @param <E> type of {@link PricedShippableItem} that this container contains.
 */
public interface PricedShippableItemContainer<E extends PricedShippableItem> extends ShippableItemContainer<E> {

}
