/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.shipping.connectivity.dto.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.CompareToBuilder;

import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Generic implementation of {@link ShippableItemContainer}.
 *
 * @param <E> type of {@link ShippableItem} that this container contains.
 */
public class ShippableItemContainerImpl<E extends ShippableItem>
		implements ShippableItemContainer<E>, Comparable<ShippableItemContainer<E>>, Serializable {

	private static final long serialVersionUID = 5000000001L;

	private Currency currency;

	private Collection<E> shippableItems;

	private ShippingAddress destinationAddress;

	private ShippingAddress originAddress;

	private String storeCode;

	private Locale locale;

	private Map<String, Object> fields;

	@Override
	public Currency getCurrency() {
		return this.currency;
	}

	@Override
	public Collection<E> getShippableItems() {
		return this.shippableItems;
	}

	@Override
	public ShippingAddress getDestinationAddress() {
		return this.destinationAddress;
	}

	@Override
	public ShippingAddress getOriginAddress() {
		return this.originAddress;
	}

	@Override
	public String getStoreCode() {
		return this.storeCode;
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public Map<String, Object> getFields() {
		return Collections.unmodifiableMap(getModifiableFields());
	}

	/**
	 * Gets the modifiable fields.
	 *
	 * @return the modifiable fields.
	 */
	protected Map<String, Object> getModifiableFields() {
		if (fields == null) {
			fields = new HashMap<>();
		}
		return this.fields;
	}

	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	public void setShippableItems(final Collection<E> shippableItems) {
		this.shippableItems = shippableItems;
	}

	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	public void setDestinationAddress(final ShippingAddress destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public void setOriginAddress(final ShippingAddress originAddress) {
		this.originAddress = originAddress;
	}

	/**
	 * Sets the input metadata fields instead of existing one.
	 *
	 * @param fields the input fields.
	 */
	public void setFields(final Map<String, Object> fields) {
		final Map<String, Object> modifiableFields = getModifiableFields();

		modifiableFields.clear();

		if (MapUtils.isNotEmpty(fields)) {
			modifiableFields.putAll(fields);
		}
	}

	/**
	 * Sets the metadata field.
	 *
	 * @param key   the key of field
	 * @param value the value of field
	 */
	public void setField(final String key, final Object value) {
		getModifiableFields().put(key, value);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int compareTo(final ShippableItemContainer<E> other) {

		if (other == null) {
			return -1;
		}

		if (this == other) {
			return 0;
		}

		return new CompareToBuilder()
				.append(getCurrency(), other.getCurrency())
				.append(getShippableItems(), other.getShippableItems())
				.append(getStoreCode(), other.getStoreCode())
				.append(getLocale(), other.getLocale())
				.append(getDestinationAddress(), other.getDestinationAddress())
				.append(getOriginAddress(), other.getOriginAddress())
				.append(getFields(), other.getFields())
				.toComparison();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ShippableItemContainer)) {
			return false;
		}

		final ShippableItemContainer<E> other = (ShippableItemContainer<E>) obj;

		return Objects.equals(getCurrency(), other.getCurrency())
				&& Objects.equals(getShippableItems(), other.getShippableItems())
				&& Objects.equals(getStoreCode(), other.getStoreCode())
				&& Objects.equals(getLocale(), other.getLocale())
				&& Objects.equals(getDestinationAddress(), other.getDestinationAddress())
				&& Objects.equals(getOriginAddress(), other.getOriginAddress())
				&& Objects.equals(getFields(), other.getFields());
	}

	@Override
	public int hashCode() {

		return Objects.hash(
				getCurrency(),
				getShippableItems(),
				getStoreCode(),
				getLocale(),
				getDestinationAddress(),
				getOriginAddress(),
				getFields());
	}

}
