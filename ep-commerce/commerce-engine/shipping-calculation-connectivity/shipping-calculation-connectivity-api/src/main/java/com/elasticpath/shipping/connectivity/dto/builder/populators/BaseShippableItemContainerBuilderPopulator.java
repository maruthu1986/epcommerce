/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.populators;

import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * A base interface defining population methods for building a {@link com.elasticpath.shipping.connectivity.dto.ShippableItemContainer} that
 * do not rely on the generic {@link com.elasticpath.shipping.connectivity.dto.ShippableItem} type (e.g. unpriced vs priced).
 *
 * Definining this base interface is important for Populator visitors as it allows a priced Populator instance to be passed to a non-priced
 * Visitor (and just have its non-priced fields set) due to inheritance. This doesn't work when interfaces have generic types.
 *
 * @see com.elasticpath.service.shipping.transformers.impl.BaseShippableItemContainerTransformerImpl for where this is used.
 */
public interface BaseShippableItemContainerBuilderPopulator {

	/**
	 * Sets the currency of the items.
	 *
	 * @param currency the currency
	 * @return this populator.
	 */
	BaseShippableItemContainerBuilderPopulator withCurrency(Currency currency);

	/**
	 * @return currently populated currency.
	 */
	Currency getCurrency();

	/**
	 * Sets the Store code.
	 *
	 * @param storeCode the Store code.
	 * @return this populator.
	 */
	BaseShippableItemContainerBuilderPopulator withStoreCode(String storeCode);

	/**
	 * @return currently populated Store code.
	 */
	String getStoreCode();

	/**
	 * Sets the locale.
	 *
	 * @param locale the locale
	 * @return this populator.
	 */
	BaseShippableItemContainerBuilderPopulator withLocale(Locale locale);

	/**
	 * @return currently populated locale.
	 */
	Locale getLocale();

	/**
	 * Sets the destination ship-to address.
	 *
	 * @param destinationAddress the destination ship-to address.
	 * @return this populator.
	 */
	BaseShippableItemContainerBuilderPopulator withDestinationAddress(ShippingAddress destinationAddress);

	/**
	 * @return currently populated destination ship-to address.
	 */
	ShippingAddress getDestinationAddress();

	/**
	 * Sets the origin address.
	 *
	 * @param originAddress the origin address
	 * @return this populator.
	 */
	BaseShippableItemContainerBuilderPopulator withOriginAddress(ShippingAddress originAddress);

	/**
	 * @return currently populated origin address.
	 */
	ShippingAddress getOriginAddress();

	/**
	 * Sets the fields.
	 *
	 * @param fields the metadata fields.
	 * @return thøe builder itself
	 */
	BaseShippableItemContainerBuilderPopulator withFields(Map<String, Object> fields);

	/**
	 * @return currently populated metadata fields.
	 */
	Map<String, Object> getFields();

	/**
	 * Sets the field.
	 *
	 * @param key   the key of field.
	 * @param value the value of field.
	 * @return this populator..
	 */
	BaseShippableItemContainerBuilderPopulator withField(String key, Object value);
}
