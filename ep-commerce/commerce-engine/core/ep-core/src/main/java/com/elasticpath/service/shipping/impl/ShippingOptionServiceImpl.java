/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shipping.builder.ShippingOptionResultBuilder;
import com.elasticpath.service.shipping.transformers.ShippableItemContainerTransformer;
import com.elasticpath.service.shipping.transformers.ShippingAddressTransformer;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;

/**
 * Default implementation of {@link ShippingOptionService}.
 */
public class ShippingOptionServiceImpl implements ShippingOptionService {
	private ShippingCalculationService shippingCalculationService;
	private Supplier<ShippingOptionResultBuilder> shippingOptionResultBuilderSupplier;
	private ShippingAddressTransformer shippingAddressTransformer;
	private ShippableItemContainerTransformer shippableItemContainerTransformer;

	@Override
	public ShippingOptionResult getShippingOptions(final ShoppingCart shoppingCart) {
		final ShippableItemContainer<?> shippableItemContainer = shippableItemContainerTransformer.apply(shoppingCart);
		return createResult(getShippingCalculationService().getUnpricedShippingOptions(shippableItemContainer));
	}

	@Override
	public ShippingOptionResult getShippingOptions(final Address destination, final String storeCode, final Locale locale) {
		final ShippingAddress shippingAddress = shippingAddressTransformer.apply(destination);
		return createResult(getShippingCalculationService().getUnpricedShippingOptions(shippingAddress, storeCode, locale));
	}

	@Override
	public Optional<ShippingOption> getDefaultShippingOption(final List<ShippingOption> availableShippingOptions) {
		// simply return the first option as default shipping option.
		return availableShippingOptions.stream().findFirst();
	}

	@Override
	public ShippingOptionResult getAllShippingOptions(final String storeCode, final Locale locale) {
		return createResult(getShippingCalculationService().getAllShippingOptions(storeCode, locale));
	}

	/**
	 * Factory method for creating a {@link ShippingOptionResult} from the given {@link ShippingCalculationResult}.
	 *
	 * @param shippingCalculationResult the shipping calculation result to create the {@link ShippingOptionResult} from.
	 * @return a {@link ShippingOptionResult} from the information contained in the given {@link ShippingCalculationResult}.
	 */
	private ShippingOptionResult createResult(final ShippingCalculationResult shippingCalculationResult) {
		return getShippingOptionResultBuilderSupplier().get()
				.from(shippingCalculationResult)
				.build();
	}

	private ShippingCalculationService getShippingCalculationService() {
		return this.shippingCalculationService;
	}

	public void setShippingCalculationService(final ShippingCalculationService shippingCalculationService) {
		this.shippingCalculationService = shippingCalculationService;
	}

	private Supplier<ShippingOptionResultBuilder> getShippingOptionResultBuilderSupplier() {
		return this.shippingOptionResultBuilderSupplier;
	}

	public void setShippingOptionResultBuilderSupplier(final Supplier<ShippingOptionResultBuilder> shippingOptionResultBuilderSupplier) {
		this.shippingOptionResultBuilderSupplier = shippingOptionResultBuilderSupplier;
	}

	protected ShippingAddressTransformer getShippingAddressTransformer() {
		return this.shippingAddressTransformer;
	}

	public void setShippingAddressTransformer(final ShippingAddressTransformer shippingAddressTransformer) {
		this.shippingAddressTransformer = shippingAddressTransformer;
	}

	protected ShippableItemContainerTransformer getShippableItemContainerTransformer() {
		return this.shippableItemContainerTransformer;
	}

	public void setShippableItemContainerTransformer(final ShippableItemContainerTransformer shippableItemContainerTransformer) {
		this.shippableItemContainerTransformer = shippableItemContainerTransformer;
	}
}
