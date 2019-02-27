/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Interface defining builder of {@link ShippingOption}.
 */
public interface ShippingOptionBuilder extends Builder<ShippingOption, ShippingOptionBuilder> {

	/**
	 * Returns builder itself with shipping option code.
	 *
	 * @param shippingOptionCode shipping option code.
	 * @return the builder itself
	 */
	ShippingOptionBuilder withCode(String shippingOptionCode);

	/**
	 * Returns builder itself with shipping option display name.
	 *
	 * @param shippingOptionDisplayNames display name map
	 * @return the builder itself
	 */
	ShippingOptionBuilder withDisplayNames(Map<Locale, String> shippingOptionDisplayNames);

	/**
	 * Returns builder itself with shipping option description.
	 *
	 * @param shippingOptionDescription description
	 * @return the builder itself
	 */
	ShippingOptionBuilder withDescription(String shippingOptionDescription);

	/**
	 * Returns builder itself with carrier code.
	 *
	 * @param carrierCode carrier code
	 * @return the builder itself
	 */
	ShippingOptionBuilder withCarrierCode(String carrierCode);

	/**
	 * Returns builder itself with carrier name.
	 *
	 * @param carrierDisplayName carrier name
	 * @return the builder itself
	 */
	ShippingOptionBuilder withCarrierDisplayName(String carrierDisplayName);

	/**
	 * Returns builder itself with shipping cost.
	 *
	 * @param shippingCost shipping cost
	 * @return the builder itself
	 */
	ShippingOptionBuilder withShippingCost(Money shippingCost);

	/**
	 * Returns builder itself with metadata fields.
	 *
	 * @param fields the metadata
	 * @return the builder itself
	 */
	ShippingOptionBuilder withFields(Map<String, Object> fields);

	/**
	 * Returns builder itself with metadata field.
	 *
	 * @param key   the key of field
	 * @param value the value of field
	 * @return the builder itself.
	 */
	ShippingOptionBuilder withField(String key, Object value);

	/**
	 * Returns builder itself with estimated earliest delivery date.
	 *
	 * @param estimatedEarliestDeliveryDate the estimated earliest delivery date
	 * @return the builder itself
	 */
	ShippingOptionBuilder withEstimatedEarliestDeliveryDate(LocalDate estimatedEarliestDeliveryDate);

	/**
	 * Returns builder itself with estimated latest delivery date.
	 *
	 * @param estimatedLatestDeliveryDate estimated latest delivery date
	 * @return the builder itself
	 */
	ShippingOptionBuilder withEstimatedLatestDeliveryDate(LocalDate estimatedLatestDeliveryDate);
}
