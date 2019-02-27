/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.plugin.shipping.impl;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shipping.ShippingOptionTransformer;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippableItem;
import com.elasticpath.shipping.connectivity.dto.ShippableItemContainer;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.spi.AbstractShippingCalculationPlugin;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingCostCalculationCapability;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingOptionListAllCapability;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingOptionListCapability;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingOptionListPerDestinationCapability;

/**
 * Implementation of shipping calculation provider to resolve priced and unpriced {@link ShippingOption}s.
 */
public class EpShippingCalculationPluginImpl extends AbstractShippingCalculationPlugin implements
		ShippingCostCalculationCapability,
		ShippingOptionListCapability,
		ShippingOptionListAllCapability,
		ShippingOptionListPerDestinationCapability {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;

	private static final String EP_SHIPPING_PROVIDER_PLUGIN_NAME = "epShippingCalculationPlugin";

	private transient ShippingServiceLevelService shippingServiceLevelService;
	private transient ProductSkuLookup productSkuLookup;
	private transient ShippingOptionTransformer shippingOptionTransformer;

	@Override
	public String getName() {
		return EP_SHIPPING_PROVIDER_PLUGIN_NAME;
	}

	@Override
	public List<ShippingOption> getAllShippingOptions(final String storeCode, final Locale locale) {
		return getShippingOptions(
				() -> shippingServiceLevelService.findByStore(storeCode),
				shippingServiceLevel -> transformIntoUnpricedShippingOption(shippingServiceLevel, locale));
	}

	@Override
	public List<ShippingOption> getUnpricedShippingOptions(final ShippableItemContainer<?> container) {
		// The OOTB {@link ShippingServiceLevel} objects are configured only based on store, address, so delegate to that method below.
		return getUnpricedShippingOptions(container.getDestinationAddress(), container.getStoreCode(), container.getLocale());
	}

	@Override
	public List<ShippingOption> getUnpricedShippingOptions(final ShippingAddress destinationAddress,
														   final String storeCode,
														   final Locale locale) {

		return getShippingOptions(() -> getShippingServiceLevels(storeCode, destinationAddress),
								  shippingServiceLevel -> transformIntoUnpricedShippingOption(shippingServiceLevel, locale));
	}

	/**
	 * Fetch {@link ShippingServiceLevel} by given fetcher and transform it to {@link ShippingOption} by given transformer.
	 *
	 * @param shippingServiceLevelsSupplier the shipping service level supplier.
	 * @param shippingOptionTransformer the shipping option transformer.
	 * @return the list of {@link ShippingOption}s.
	 */
	protected List<ShippingOption> getShippingOptions(final Supplier<List<ShippingServiceLevel>> shippingServiceLevelsSupplier,
													  final Function<ShippingServiceLevel, ShippingOption> shippingOptionTransformer) {

		final List<ShippingServiceLevel> shippingServiceLevels = shippingServiceLevelsSupplier.get();
		return shippingServiceLevels.stream()
				.map(shippingOptionTransformer)
				.collect(Collectors.toList());
	}

	@Override
	public List<ShippingOption> getPricedShippingOptions(final PricedShippableItemContainer<?> container) {
		final Money shippableItemsSubtotal = calculateShippableItemsSubtotal(container.getShippableItems(), container.getCurrency());

		return getShippingOptions(
				() -> getShippingServiceLevels(container.getStoreCode(), container.getDestinationAddress()),
				shippingServiceLevel -> transformIntoPricedShippingOption(shippingServiceLevel, container, shippableItemsSubtotal));
	}

	/**
	 * Returns the corresponding {@link ShippingServiceLevel} objects matching the given store and destination address.
	 *
	 * @param storeCode the Store code to limit the applicable {@link ShippingServiceLevel} objects to.
	 * @param destinationAddress the destination address to limit the applicable {@link ShippingServiceLevel} objects to.
	 * @return the corresponding {@link ShippingServiceLevel} objects matching the given store and destination address.
	 */
	protected List<ShippingServiceLevel> getShippingServiceLevels(final String storeCode, final ShippingAddress destinationAddress) {
		return shippingServiceLevelService.retrieveShippingServiceLevel(storeCode, destinationAddress);
	}

	/**
	 * Transform {@link ShippingServiceLevel} to {@link ShippingOption} without populating shipping cost.
	 *
	 * @param shippingServiceLevel the shipping service level.
	 * @param locale               the locale
	 * @return the populated {@link ShippingOption} without shipping cost.
	 */
	protected ShippingOption transformIntoUnpricedShippingOption(final ShippingServiceLevel shippingServiceLevel, final Locale locale) {
		return shippingOptionTransformer.transform(shippingServiceLevel, () -> null, locale);
	}

	/**
	 * Transform {@link ShippingServiceLevel} to {@link ShippingOption} including populating shipping cost.
	 *
	 * @param shippingServiceLevel {@link ShippingServiceLevel} to transform.
	 * @param container container containing items needed to calculate shipping cost.
	 * @param shippableItemsSubtotal the subtotal of all shippable items.
	 * @return the populated {@link ShippingOption} including shipping cost.
	 */
	protected ShippingOption transformIntoPricedShippingOption(final ShippingServiceLevel shippingServiceLevel,
															   final PricedShippableItemContainer<?> container, final Money shippableItemsSubtotal) {
		final Supplier<Money> shippingCostSupplier = () -> calculateShippingCost(shippingServiceLevel, container.getShippableItems(),
																				 shippableItemsSubtotal, container.getCurrency());

		return shippingOptionTransformer.transform(shippingServiceLevel, shippingCostSupplier, container.getLocale());
	}

	/**
	 * Calculates the subtotal of shippable items.
	 *
	 * @param shippableItems the shippable items
	 * @param currency       the currency
	 * @return the subtotal
	 */
	protected Money calculateShippableItemsSubtotal(final Collection<? extends PricedShippableItem> shippableItems, final Currency currency) {
		return shippableItems.stream()
				.map(PricedShippableItem::getTotalPrice)
				.reduce(Money.zero(currency), Money::add);
	}

	/**
	 * Calculates shipping cost.
	 *
	 * @param shippingServiceLevel   the shipping service level
	 * @param shippableItems         the shippable items
	 * @param shippableItemsSubtotal the shipping item subtotal
	 * @param currency               the currency
	 * @return the shipping cost
	 */
	protected Money calculateShippingCost(final ShippingServiceLevel shippingServiceLevel,
										  final Collection<? extends ShippableItem> shippableItems,
										  final Money shippableItemsSubtotal,
										  final Currency currency) {
		return shippingServiceLevel.getShippingCostCalculationMethod()
				.calculateShippingCost(shippableItems, shippableItemsSubtotal, currency, productSkuLookup);
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}


	public void setShippingServiceLevelService(final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

	public void setShippingOptionTransformer(final ShippingOptionTransformer shippingOptionTransformer) {
		this.shippingOptionTransformer = shippingOptionTransformer;
	}
}
