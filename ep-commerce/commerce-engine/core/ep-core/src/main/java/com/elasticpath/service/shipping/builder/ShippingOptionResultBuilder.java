/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.builder;

import java.util.List;

import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.builder.Builder;

/**
 * Interface defining builder of {@link ShippingOptionResult}.
 */
public interface ShippingOptionResultBuilder extends Builder<ShippingOptionResult, ShippingOptionResultBuilder> {

	/**
	 * Returns impl instance populated from the given {@link ShippingCalculationResult}.
	 *
	 * @param shippingCalculationResult the result of the shipping calculation that this result is based off.
	 * @return instance of impl
	 */
	ShippingOptionResultBuilder from(ShippingCalculationResult shippingCalculationResult);

	/**
	 * Returns impl instance with shipping options set.
	 *
	 * @param shippingOptions list of {@link ShippingOption}
	 * @return instance of impl
	 */
	ShippingOptionResultBuilder withShippingOptions(List<ShippingOption> shippingOptions);

	/**
	 * Returns impl instance with error information (in the case that the result represents an unsuccessful request) set.
	 *
	 * @param errorInformation the error information.
	 * @return instance of impl
	 */
	ShippingOptionResultBuilder withErrorInformation(ShippingCalculationResult.ErrorInformation errorInformation);
}
