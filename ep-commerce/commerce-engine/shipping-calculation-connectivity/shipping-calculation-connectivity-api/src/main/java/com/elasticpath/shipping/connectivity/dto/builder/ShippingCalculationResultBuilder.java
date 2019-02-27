/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder;

import java.util.List;

import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Interface defining builder of {@link ShippingCalculationResult}.
 */
public interface ShippingCalculationResultBuilder extends Builder<ShippingCalculationResult, ShippingCalculationResultBuilder> {

	/**
	 * Returns impl instance with shipping options set.
	 *
	 * @param shippingOptions list of {@link ShippingOption}
	 * @return instance of impl
	 */
	ShippingCalculationResultBuilder withShippingOptions(List<ShippingOption> shippingOptions);

	/**
	 * Returns impl instance with error information (in the case that the result represents an unsuccessful request) set.
	 *
	 * @param errorInformation the error information.
	 * @return instance of impl
	 */
	ShippingCalculationResultBuilder withErrorInformation(ShippingCalculationResult.ErrorInformation errorInformation);

}
