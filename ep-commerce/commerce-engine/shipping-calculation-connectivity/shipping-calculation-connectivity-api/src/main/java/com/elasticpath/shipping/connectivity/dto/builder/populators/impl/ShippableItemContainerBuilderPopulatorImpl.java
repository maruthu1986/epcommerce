/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.populators.impl;

import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.builder.populators.ShippableItemContainerBuilderPopulator;
import com.elasticpath.shipping.connectivity.dto.impl.ShippableItemContainerImpl;

/**
 * Generic implementation of {@link ShippableItemContainerBuilderPopulator} to build {@link ShippableItemContainer}.
 *
 * @param <I> interface type of the instance being built; an instance of {@link ShippableItemContainer}.
 * @param <E> interface type of the shippable items contained by the instance being built; extends {@link ShippableItem}.
 * @param <C> concrete type of the instance being built; an instance of {@link ShippableItemContainerImpl}.
 * @param <P> interface type of this Populator; an instance of {@link ShippableItemContainerBuilderPopulator}.
 */
public class ShippableItemContainerBuilderPopulatorImpl<I extends ShippableItemContainer<E>,
														E extends ShippableItem,
														C extends ShippableItemContainerImpl<E>,
														P extends ShippableItemContainerBuilderPopulator<E>>
		extends AbstractRespawnBuilderPopulatorImpl<I, C, P> implements ShippableItemContainerBuilderPopulator<E> {

	@Override
	protected void copy(final I externalInstance) {
		withShippableItems(externalInstance.getShippableItems());
		withCurrency(externalInstance.getCurrency());
		withDestinationAddress(externalInstance.getDestinationAddress());
		withOriginAddress(externalInstance.getOriginAddress());
		withStoreCode(externalInstance.getStoreCode());
		withLocale(externalInstance.getLocale());
		withFields(externalInstance.getFields());
	}

	@Override
	public P withShippableItems(final Collection<E> shippableItems) {
		getInstanceUnderBuild().setShippableItems(shippableItems);
		return self();
	}

	@Override
	public Collection<E> getShippableItems() {
		return getInstanceUnderBuild().getShippableItems();
	}

	@Override
	public P withCurrency(final Currency currency) {
		getInstanceUnderBuild().setCurrency(currency);
		return self();
	}

	@Override
	public Currency getCurrency() {
		return getInstanceUnderBuild().getCurrency();
	}

	@Override
	public P withStoreCode(final String storeCode) {
		getInstanceUnderBuild().setStoreCode(storeCode);
		return self();
	}

	@Override
	public String getStoreCode() {
		return getInstanceUnderBuild().getStoreCode();
	}

	@Override
	public P withLocale(final Locale locale) {
		getInstanceUnderBuild().setLocale(locale);
		return self();
	}

	@Override
	public Locale getLocale() {
		return getInstanceUnderBuild().getLocale();
	}

	@Override
	public P withDestinationAddress(final ShippingAddress destinationAddress) {
		getInstanceUnderBuild().setDestinationAddress(destinationAddress);
		return self();
	}

	@Override
	public ShippingAddress getDestinationAddress() {
		return getInstanceUnderBuild().getDestinationAddress();
	}

	@Override
	public P withOriginAddress(final ShippingAddress originAddress) {
		getInstanceUnderBuild().setOriginAddress(originAddress);
		return self();
	}

	@Override
	public ShippingAddress getOriginAddress() {
		return getInstanceUnderBuild().getOriginAddress();
	}

	@Override
	public P withFields(final Map<String, Object> fields) {
		getInstanceUnderBuild().setFields(fields);
		return self();
	}

	@Override
	public Map<String, Object> getFields() {
		return getInstanceUnderBuild().getFields();
	}

	@Override
	public P withField(final String key, final Object value) {
		getInstanceUnderBuild().setField(key, value);
		return self();
	}
}
