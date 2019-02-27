/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingCalculationResultBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.populators.impl.AbstractRespawnBuilderPopulatorImpl;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingCalculationResultImpl;

/**
 * {@link ShippingCalculationResult} impl implementation.
 */
public class ShippingCalculationResultBuilderImpl
		extends AbstractRespawnBuilderPopulatorImpl<ShippingCalculationResult, ShippingCalculationResultImpl, ShippingCalculationResultBuilder>
		implements ShippingCalculationResultBuilder {

	@Override
	public void copy(final ShippingCalculationResult externalInstance) {
		withShippingOptions(externalInstance.getAvailableShippingOptions());
		externalInstance.getErrorInformation().ifPresent(this::withErrorInformation);
	}

	@Override
	public ShippingCalculationResultBuilder withShippingOptions(final List<ShippingOption> shippingOptions) {
		getInstanceUnderBuild().setAvailableShippingOptions(shippingOptions);
		return this;
	}

	@Override
	public ShippingCalculationResultBuilder withErrorInformation(final ShippingCalculationResult.ErrorInformation errorInformation) {
		getInstanceUnderBuild().setErrorInformation(errorInformation);
		return this;
	}

	@Override
	public ShippingCalculationResultBuilder getPopulator() {
		return this;
	}

	@Override
	protected Optional<Supplier<ShippingCalculationResultImpl>> createDefaultInstanceSupplier() {
		return Optional.of(ShippingCalculationResultImpl::new);
	}
}
