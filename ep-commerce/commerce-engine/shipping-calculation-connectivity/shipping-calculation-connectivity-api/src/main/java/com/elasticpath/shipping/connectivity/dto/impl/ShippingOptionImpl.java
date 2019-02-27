/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.Maps;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.CompareToBuilder;

import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Default shippingOption implementation.
 */
public class ShippingOptionImpl implements ShippingOption, Comparable<ShippingOption>, Serializable {

	private static final long serialVersionUID = 5000000001L;

	private String code;
	private Map<Locale, String> displayNames;
	private String description;
	private String carrierCode;
	private String carrierDisplayName;
	private Money shippingCost;
	private Map<String, Object> fields;

	private LocalDate estimatedEarliestDeliveryDate;
	private LocalDate estimatedLatestDeliveryDate;

	@Override
	public String getCode() {
		return this.code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	@Override
	public Optional<String> getDisplayName(final Locale locale) {
		return Optional.ofNullable(getModifiableDisplayNames().get(locale))
				.map(Optional::of)
				.orElse(Optional.ofNullable(getModifiableDisplayNames().get(Locale.forLanguageTag(locale.getLanguage()))));
	}

	/**
	 * Sets the shipping option display name and its locale.
	 *
	 * @param locale      the locale of display name.
	 * @param displayName the name for display.
	 */
	public void setDisplayName(final Locale locale, final String displayName) {
		getModifiableDisplayNames().put(locale, displayName);
	}

	/**
	 * Sets the given display names of shipping option instead the existing one.
	 *
	 * @param displayNames the map of shipping option display name.
	 */
	public void setDisplayNames(final Map<Locale, String> displayNames) {
		if (displayNames == null) {
			return;
		}
		getModifiableDisplayNames().clear();
		getModifiableDisplayNames().putAll(displayNames);
	}

	/**
	 * Gets the modifiable display name of shipping option.
	 *
	 * @return the modifiable display name map.
	 */
	protected Map<Locale, String> getModifiableDisplayNames() {
		if (this.displayNames == null) {
			this.displayNames = Maps.newHashMap();
		}
		return this.displayNames;
	}

	@Override
	public Map<Locale, String> getDisplayNames() {
		return Collections.unmodifiableMap(getModifiableDisplayNames());
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public Optional<String> getCarrierCode() {
		return Optional.ofNullable(carrierCode);
	}

	public void setCarrierCode(final String carrierCode) {
		this.carrierCode = carrierCode;
	}

	@Override
	public Optional<String> getCarrierDisplayName() {
		return Optional.ofNullable(carrierDisplayName);
	}

	public void setCarrierDisplayName(final String carrierDisplayName) {
		this.carrierDisplayName = carrierDisplayName;
	}

	@Override
	public Optional<Money> getShippingCost() {
		return Optional.ofNullable(shippingCost);
	}

	public void setShippingCost(final Money shippingCost) {
		this.shippingCost = shippingCost;
	}

	@Override
	public Map<String, Object> getFields() {
		return Collections.unmodifiableMap(getModifiableFields());
	}

	@Override
	public Optional<LocalDate> getEstimatedEarliestDeliveryDate() {
		return Optional.ofNullable(estimatedEarliestDeliveryDate);
	}

	public void setEstimatedEarliestDeliveryDate(final LocalDate estimatedEarliestDeliveryDate) {
		this.estimatedEarliestDeliveryDate = estimatedEarliestDeliveryDate;
	}

	@Override
	public Optional<LocalDate> getEstimatedLatestDeliveryDate() {
		return Optional.ofNullable(estimatedLatestDeliveryDate);
	}

	public void setEstimatedLatestDeliveryDate(final LocalDate estimatedLatestDeliveryDate) {
		this.estimatedLatestDeliveryDate = estimatedLatestDeliveryDate;
	}

	/**
	 * Gets the modifiable fields of shipping option.
	 *
	 * @return the modifiable fields.
	 */
	protected Map<String, Object> getModifiableFields() {
		if (fields == null) {
			fields = Maps.newHashMap();
		}
		return this.fields;
	}

	/**
	 * Sets metadata field.
	 *
	 * @param fieldKey   key of the field.
	 * @param fieldValue value of the field.
	 */
	public void setField(final String fieldKey, final Object fieldValue) {
		getModifiableFields().put(fieldKey, fieldValue);
	}

	/**
	 * Sets metadata fields instead of existing one.
	 *
	 * @param fields the input metadata fields.
	 */
	public void setFields(final Map<String, Object> fields) {
		if (fields == null) {
			return;
		}
		getModifiableFields().clear();
		getModifiableFields().putAll(fields);
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ShippingOption)) {
			return false;
		}

		final ShippingOption other = (ShippingOption) obj;

		return Objects.equals(getCode(), other.getCode())
				&& Objects.equals(getCarrierCode(), other.getCarrierCode())
				&& Objects.equals(getDescription(), other.getDescription())
				&& Objects.equals(getShippingCost(), other.getShippingCost())
				&& Objects.equals(getDisplayNames(), other.getDisplayNames())
				&& Objects.equals(getCarrierDisplayName(), other.getCarrierDisplayName())
				&& Objects.equals(getFields(), other.getFields())
				&& Objects.equals(getEstimatedEarliestDeliveryDate(), other.getEstimatedEarliestDeliveryDate())
				&& Objects.equals(getEstimatedLatestDeliveryDate(), other.getEstimatedLatestDeliveryDate());

	}

	@Override
	public int hashCode() {
		return Objects.hash(
				getCode(),
				getCarrierCode(),
				getDescription(),
				getShippingCost(),
				getDisplayNames(),
				getCarrierDisplayName(),
				getFields(),
				getEstimatedEarliestDeliveryDate(),
				getEstimatedLatestDeliveryDate());
	}

	@Override
	public int compareTo(final ShippingOption other) {
		if (other == null) {
			return -1;
		}

		// Compare with display names, falling back to codes if not, grouping options by carrier.
		return new CompareToBuilder()
				.append(getCarrierDisplayName(), other.getCarrierDisplayName())
				.append(getCarrierCode(), other.getCarrierCode())
				.append(getDisplayNames(), other.getDisplayNames())
				.append(getCode(), other.getCode())
				.append(getEstimatedEarliestDeliveryDate(), other.getEstimatedEarliestDeliveryDate())
				.append(getEstimatedLatestDeliveryDate(), other.getEstimatedLatestDeliveryDate())
				.toComparison();
	}
}
