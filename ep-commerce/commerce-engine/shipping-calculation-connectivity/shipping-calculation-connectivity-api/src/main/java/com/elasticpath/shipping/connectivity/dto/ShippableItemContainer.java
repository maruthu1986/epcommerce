/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto;

import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

/**
 * Generic type interface defining a container which contains information for shipping calculation.
 *
 * @param <E> type of {@link ShippableItem} that this container contains.
 */
public interface ShippableItemContainer<E extends ShippableItem> {

	/**
	 * Gets the currency of the items.
	 *
	 * @return the currency.
	 */
	Currency getCurrency();

	/**
	 * Gets the collection of shippable item.
	 *
	 * @return the shippable item collection.
	 */
	Collection<E> getShippableItems();

	/**
	 * Gets the destination address of shipping.
	 *
	 * @return the destination address.
	 */
	ShippingAddress getDestinationAddress();

	/**
	 * Gets the origin address of shipping.
	 *
	 * @return the origin address.
	 */
	ShippingAddress getOriginAddress();

	/**
	 * Gets the store code.
	 *
	 * @return the store code.
	 */
	String getStoreCode();

	/**
	 * Gets the locale of store.
	 *
	 * @return the locale.
	 */
	Locale getLocale();

	/**
	 * Gets the unmodifiable fields which stores metadata for extension.
	 *
	 * @return the unmodifiable metadata fields.
	 */
	Map<String, Object> getFields();
}
