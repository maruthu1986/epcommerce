/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.elasticpath.money.Money;

/**
 * ShippingOption interface.
 */
public interface ShippingOption {

	/**
	 * Returns shipping option code.
	 *
	 * @return code, never {@code null}.
	 */
	String getCode();

	/**
	 * Returns shipping option display name by given locale.
	 *
	 * @param locale the locale
	 * @return name, never {@code null}.
	 */
	Optional<String> getDisplayName(Locale locale);

	/**
	 * Returns a map of shipping option display name.
	 *
	 * @return the map of shipping option display name.
	 */
	Map<Locale, String> getDisplayNames();

	/**
	 * Returns description for this {@link ShippingOption} if available.
	 *
	 * @return an {@link Optional} containing the description if provided.
	 */
	Optional<String> getDescription();

	/**
	 * Returns carrier code for this {@link ShippingOption} if available.
	 *
	 * @return an {@link Optional} containing the carrier code if provided.
	 */
	Optional<String> getCarrierCode();

	/**
	 * Returns carrier name for this {@link ShippingOption} if available.
	 *
	 * @return an {@link Optional} containing the carrier name if provided.
	 */
	Optional<String> getCarrierDisplayName();

	/**
	 * Returns shipping cost for this {@link ShippingOption} if available, unpriced calls may not return a priced ShippingOption.
	 *
	 * @return an {@link Optional} containing the shipping cost if provided.
	 */
	Optional<Money> getShippingCost();

	/**
	 * Provides an immutable container to hold extra data for the shipping option which may be populated by some custom shipping calculation
	 * providers to provide extra information over and above the standard information. Implementations should always return a non-null map.
	 *
	 * @return immutable map of all key/value data field pairs, never {@code null}.
	 */
	Map<String, Object> getFields();

	/**
	 * Returns the estimated earliest delivery date that this shipping option can provide.
	 *
	 * @return the earliest estimated delivery date
	 */
	Optional<LocalDate> getEstimatedEarliestDeliveryDate();

	/**
	 * Returns the estimated latest delivery date that this shipping option would provide.
	 *
	 * @return the latest estimated delivery date
	 */
	Optional<LocalDate> getEstimatedLatestDeliveryDate();
}
