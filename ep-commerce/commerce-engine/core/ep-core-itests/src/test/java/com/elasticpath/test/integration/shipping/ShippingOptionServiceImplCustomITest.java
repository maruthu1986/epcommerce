/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.integration.shipping;

import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import javax.inject.Provider;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingCalculationResultBuilder;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingOptionBuilder;
import com.elasticpath.shipping.connectivity.service.selector.impl.StaticPricedShippingCalculationPluginSelectorImpl;
import com.elasticpath.shipping.connectivity.service.selector.impl.StaticUnpricedShippingCalculationPluginSelectorImpl;
import com.elasticpath.shipping.connectivity.spi.AbstractShippingCalculationPlugin;
import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingCalculationCapability;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingCostCalculationCapability;
import com.elasticpath.shipping.connectivity.spi.capability.ShippingOptionListCapability;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests the shipping option service using a custom shipping implementation.
 */
public class ShippingOptionServiceImplCustomITest extends AbstractShippingTestCase {

	@Resource
	private ShippingOptionService shippingOptionService;

	@Resource
	private StaticPricedShippingCalculationPluginSelectorImpl staticPricedShippingCalculationProviderSelector;

	@Resource
	private StaticUnpricedShippingCalculationPluginSelectorImpl staticUnpricedShippingCalculationProviderSelector;

	@Resource
	private Provider<ShippingOptionBuilder> shippingOptionBuilderProvider;

	@Resource
	private Provider<ShippingCalculationResultBuilder> shippingCalculationResultBuilderProvider;

	@Resource(name = "unpricedToPricedShippingCalculationPlugin")
	private ShippingCalculationPlugin unpricedToPricedShippingCalculationPlugin;

	@Resource
	private PricingSnapshotService pricingSnapshotService;

	@Resource
	private ShopperService shopperService;

	@Resource
	private ShoppingCartService shoppingCartService;

	@Resource
	private CustomerSessionService customerSessionService;

	private static final Locale LOCALE = Locale.CANADA;

	private List<ShippingOption> expectedUnpricedShippingOptions;
	private List<ShippingOption> expectedPricedShippingOptions;

	@Before
	public void setUp() {
		expectedUnpricedShippingOptions = createExpectedShippingCalculationResult(5);
		expectedPricedShippingOptions = createExpectedShippingCalculationResult(3);

		staticPricedShippingCalculationProviderSelector
				.setShippingCalculationPluginList(singletonList(createCustomShippingCalculationProvider()));
		staticUnpricedShippingCalculationProviderSelector
				.setShippingCalculationPluginList(singletonList(createCustomShippingCalculationProvider()));
	}

	@DirtiesDatabase
	@Test
	public void testGetShippingOptionsForCart() {
		final ShoppingCart shoppingCart = checkoutTestCartBuilder.build();

		final ShippingOptionResult shippingOptionResult = shippingOptionService.getShippingOptions(shoppingCart);
		assertThat(shippingOptionResult.isSuccessful()).isTrue();
		final List<ShippingOption> shippingOptions = shippingOptionResult.getAvailableShippingOptions();

		assertThat(shippingOptions).as("Shipping options returned does not match the expected unpriced options")
				.isEqualTo(expectedUnpricedShippingOptions);
	}

	@DirtiesDatabase
	@Test
	public void testGetShippingOptionsForCartWithUnpricedToPricedShippingCalculationPlugin() {
		final ShoppingCart shoppingCart = checkoutTestCartBuilder.build();
		staticUnpricedShippingCalculationProviderSelector
				.setShippingCalculationPluginList(singletonList(unpricedToPricedShippingCalculationPlugin));

		final ShippingOptionResult shippingOptionResult = shippingOptionService.getShippingOptions(shoppingCart);
		assertThat(shippingOptionResult.isSuccessful()).isTrue();
		final List<ShippingOption> shippingOptions = shippingOptionResult.getAvailableShippingOptions();

		assertThat(shippingOptions).as("Shipping options returned does not match the expected unpriced options")
				.isEqualTo(expectedPricedShippingOptions);
	}

	private List<ShippingOption> createExpectedShippingCalculationResult(final int numberOfEntries) {
		return IntStream.range(0, numberOfEntries)
				.mapToObj(this::createShippingOption)
				.collect(Collectors.toList());

	}

	private ShippingOption createShippingOption(final int number) {
		return shippingOptionBuilderProvider.get()
				.withCode("SHIPPING_OPTION_CODE_" + number)
				.withDisplayNames(ImmutableMap.of(LOCALE, "Unpriced Shipping Option " + number))
				.build();
	}

	private ShippingCalculationPlugin createCustomShippingCalculationProvider() {
		return new AbstractShippingCalculationPlugin() {

			private final static long serialVersionUID = 1L;

			@Override
			public String getName() {
				return "custom";
			}

			@Override
			public <T extends ShippingCalculationCapability> boolean hasCapability(final Class<T> capability) {
				return getCapability(capability) != null;
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T extends ShippingCalculationCapability> T getCapability(final Class<T> capability) {
				if (capability.equals(ShippingOptionListCapability.class)) {
					return (T) createCustomShippingOptionListCapability();
				} else if (capability.equals(ShippingCostCalculationCapability.class)) {
					return (T) createShippingCalculationCapability();
				}
				return null;
			}

			private ShippingOptionListCapability createCustomShippingOptionListCapability() {
				return (unpricedShippableItemContainer) -> expectedUnpricedShippingOptions;
			}

			private ShippingCostCalculationCapability createShippingCalculationCapability() {
				return (pricedShippableItemContainer) -> expectedPricedShippingOptions;
			}

		};
	}


}
