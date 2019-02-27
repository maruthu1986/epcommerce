/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.impl;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.money.Money;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingOptionBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.impl.AbstractRespawnBuilderPopulatorImpl;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingOptionImpl;

/**
 * {@link ShippingOption} impl implementation.
 */
public class ShippingOptionBuilderImpl
		extends AbstractRespawnBuilderPopulatorImpl<ShippingOption, ShippingOptionImpl, ShippingOptionBuilder>
		implements ShippingOptionBuilder {

	@Override
	public void copy(final ShippingOption shippingOption) {
		withCode(shippingOption.getCode());
		withDisplayNames(shippingOption.getDisplayNames());
		withDescription(shippingOption.getDescription().orElse(null));
		withCarrierCode(shippingOption.getCarrierCode().orElse(null));
		withCarrierDisplayName(shippingOption.getCarrierDisplayName().orElse(null));
		withShippingCost(shippingOption.getShippingCost().orElse(null));
		withFields(shippingOption.getFields());
		withEstimatedEarliestDeliveryDate(shippingOption.getEstimatedEarliestDeliveryDate().orElse(null));
		withEstimatedLatestDeliveryDate(shippingOption.getEstimatedLatestDeliveryDate().orElse(null));
	}

	@Override
	public ShippingOptionBuilder withCode(final String shippingOptionCode) {
		getInstanceUnderBuild().setCode(shippingOptionCode);
		return this;
	}

	@Override
	public ShippingOptionBuilder withDisplayNames(final Map<Locale, String> shippingOptionDisplayNames) {
		getInstanceUnderBuild().setDisplayNames(shippingOptionDisplayNames);
		return this;
	}

	@Override
	public ShippingOptionBuilder withDescription(final String shippingOptionDescription) {
		getInstanceUnderBuild().setDescription(shippingOptionDescription);
		return this;
	}

	@Override
	public ShippingOptionBuilder withCarrierCode(final String carrierCode) {
		getInstanceUnderBuild().setCarrierCode(carrierCode);
		return this;
	}

	@Override
	public ShippingOptionBuilder withCarrierDisplayName(final String carrierDisplayName) {
		getInstanceUnderBuild().setCarrierDisplayName(carrierDisplayName);
		return this;
	}

	@Override
	public ShippingOptionBuilder withShippingCost(final Money shippingCost) {
		getInstanceUnderBuild().setShippingCost(shippingCost);
		return this;
	}

	@Override
	public ShippingOptionBuilder withFields(final Map<String, Object> fields) {
		getInstanceUnderBuild().setFields(fields);
		return this;
	}

	@Override
	public ShippingOptionBuilder withField(final String key, final Object value) {
		getInstanceUnderBuild().setField(key, value);
		return this;
	}

	@Override
	public ShippingOptionBuilder withEstimatedEarliestDeliveryDate(final LocalDate estimatedEarliestDeliveryDate) {
		getInstanceUnderBuild().setEstimatedEarliestDeliveryDate(estimatedEarliestDeliveryDate);
		return this;
	}

	@Override
	public ShippingOptionBuilder withEstimatedLatestDeliveryDate(final LocalDate estimatedLatestDeliveryDate) {
		getInstanceUnderBuild().setEstimatedLatestDeliveryDate(estimatedLatestDeliveryDate);
		return this;
	}

	@Override
	public ShippingOptionBuilder getPopulator() {
		return this;
	}

	@Override
	protected Optional<Supplier<ShippingOptionImpl>> createDefaultInstanceSupplier() {
		return Optional.of(ShippingOptionImpl::new);
	}
}
