/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.builder.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.builder.ShippingOptionResultBuilder;
import com.elasticpath.service.shipping.impl.ShippingOptionResultImpl;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.builder.populators.impl.AbstractRespawnBuilderPopulatorImpl;

/**
 * Implementation of {@link ShippingOptionResultBuilder}.
 */
public class ShippingOptionResultBuilderImpl
		extends AbstractRespawnBuilderPopulatorImpl<ShippingOptionResult, ShippingOptionResultImpl, ShippingOptionResultBuilder>
		implements ShippingOptionResultBuilder {

	@Override
	protected void copy(final ShippingOptionResult externalInstance) {
		withShippingOptions(externalInstance.getAvailableShippingOptions());
		externalInstance.getErrorInformation().ifPresent(this::withErrorInformation);
	}

	@Override
	public ShippingOptionResultBuilder from(final ShippingCalculationResult shippingCalculationResult) {
		withShippingOptions(shippingCalculationResult.getAvailableShippingOptions());
		shippingCalculationResult.getErrorInformation().ifPresent(this::withErrorInformation);
		return this;
	}

	@Override
	public ShippingOptionResultBuilder withShippingOptions(final List<ShippingOption> shippingOptions) {
		getInstanceUnderBuild().setAvailableShippingOptions(shippingOptions);
		return this;
	}

	@Override
	public ShippingOptionResultBuilder withErrorInformation(final ShippingCalculationResult.ErrorInformation errorInformation) {
		getInstanceUnderBuild().setErrorInformation(errorInformation);
		return this;
	}

	@Override
	public ShippingOptionResultBuilder getPopulator() {
		return this;
	}

	@Override
	protected Optional<Supplier<ShippingOptionResultImpl>> createDefaultInstanceSupplier() {
		return Optional.of(ShippingOptionResultImpl::new);
	}
}
