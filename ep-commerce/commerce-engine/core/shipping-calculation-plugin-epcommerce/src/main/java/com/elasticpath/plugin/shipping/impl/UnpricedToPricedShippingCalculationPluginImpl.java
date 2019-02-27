/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.plugin.shipping.impl;

import static com.elasticpath.commons.constants.MetaDataConstants.SHOPPING_CART_KEY;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.List;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.service.shipping.transformers.PricedShippableItemContainerTransformer;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;
import com.elasticpath.shipping.connectivity.spi.AbstractShippingCalculationPlugin;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingOptionListCapability;

/**
 * A delegate class for shipping calculation providers that can only handle requests for priced shipping options.
 * This is typically the case for providers that call out to external services that require priced items in order to return the available shipping
 * options, which are also priced.
 * <p>
 * In that case an instance of this class can be injected into a {@link ShippingCalculationService} instance to automatically convert an unpriced
 * call to a priced call by calculating pricing information and then calling the {@link ShippingCalculationService} to retrieve a priced result.
 * <p>
 * We call the {@link ShippingCalculationService} to get a priced result rather than injecting a
 * {@link com.elasticpath.shipping.connectivity.spi.capability.ShippingCostCalculationCapability} directly because it makes using a caching layer
 * (such as {@link com.elasticpath.shipping.connectivity.service.cache.impl.CachingShippingCalculationServiceImpl})
 * more consistent and straight forward.
 * <p>
 */
public class UnpricedToPricedShippingCalculationPluginImpl extends AbstractShippingCalculationPlugin implements ShippingOptionListCapability {

	private static final long serialVersionUID = 1L;

	/**
	 * Plugin name.
	 */
	private static final String UNPRICED_TO_PRICED_SHIPPING_CALCULATION_PLUGIN = "unpricedToPricedShippingCalculationPlugin";

	private PricedShippableItemContainerTransformer pricedShippableItemContainerTransformer;

	private PricingSnapshotService pricingSnapshotService;

	private ShippingCalculationService shippingCalculationService;

	@Override
	public String getName() {
		return UNPRICED_TO_PRICED_SHIPPING_CALCULATION_PLUGIN;
	}

	@Override
	public List<ShippingOption> getUnpricedShippingOptions(final ShippableItemContainer<?> container) {

		final ShoppingCart shoppingCart = (ShoppingCart) container.getFields().get(SHOPPING_CART_KEY);

		requireNonNull(shoppingCart, format("Can not find shopping cart with metadata key [%s].", SHOPPING_CART_KEY));

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);

		final PricedShippableItemContainer<?> pricedShippableItemContainer =
				pricedShippableItemContainerTransformer.apply(shoppingCart, pricingSnapshot);

		return shippingCalculationService.getPricedShippingOptions(pricedShippableItemContainer).getAvailableShippingOptions();
	}

	protected PricedShippableItemContainerTransformer getPricedShippableItemContainerTransformer() {
		return this.pricedShippableItemContainerTransformer;
	}

	public void setPricedShippableItemContainerTransformer(final PricedShippableItemContainerTransformer pricedShippableItemContainerTransformer) {
		this.pricedShippableItemContainerTransformer = pricedShippableItemContainerTransformer;
	}

	protected PricingSnapshotService getPricingSnapshotService() {
		return this.pricingSnapshotService;
	}

	public void setPricingSnapshotService(final PricingSnapshotService pricingSnapshotService) {
		this.pricingSnapshotService = pricingSnapshotService;
	}

	protected ShippingCalculationService getShippingCalculationService() {
		return this.shippingCalculationService;
	}

	public void setShippingCalculationService(final ShippingCalculationService shippingCalculationService) {
		this.shippingCalculationService = shippingCalculationService;
	}
}
