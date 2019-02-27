/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.integration.shipping;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.transformers.ShippingAddressTransformer;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests the shipping option service using the default shipping implementation.
 */
public class ShippingOptionServiceImplTest extends AbstractShippingTestCase {

	@Autowired
	private ShippingOptionService shippingOptionService;

	@Autowired
	private ShippingServiceLevelService shippingServiceLevelService;

	@Autowired
	private ShippingAddressTransformer shippingAddressTransformer;

	@DirtiesDatabase
	@Test
	public void testGetShippingOptionsForCart() {
		final ShoppingCart shoppingCart = checkoutTestCartBuilder.build();

		final ShippingOptionResult shippingOptionResult = shippingOptionService.getShippingOptions(shoppingCart);
		assertThat(shippingOptionResult.isSuccessful()).isTrue();

		final List<ShippingOption> shippingOptions = shippingOptionResult.getAvailableShippingOptions();
		assertThat(shippingOptions).hasSize(1);

		final ShippingOption shippingOption = shippingOptions.get(0);
		final ShippingServiceLevel shippingServiceLevel = getShippingServiceLevel(
				storeCode,
				shippingAddressTransformer.apply(shoppingCart.getShippingAddress()));
		assertMatchingShippingOption(shippingServiceLevel, shippingOption);
	}

	@DirtiesDatabase
	@Test
	public void testGetDefaultShippingOption() {
		final ShoppingCart shoppingCart = checkoutTestCartBuilder.build();

		final ShippingOptionResult shippingOptionResult = shippingOptionService.getShippingOptions(shoppingCart);
		assertThat(shippingOptionResult.isSuccessful()).isTrue();
		final List<ShippingOption> shippingOptions = shippingOptionResult.getAvailableShippingOptions();

		final Optional<ShippingOption> defaultShippingOption = shippingOptionService.getDefaultShippingOption(shippingOptions);

		assertThat(defaultShippingOption).isPresent().contains(shippingOptions.get(0));
	}

	private void assertMatchingShippingOption(ShippingServiceLevel shippingServiceLevel, ShippingOption shippingOption) {
		assertThat(shippingServiceLevel.getCode()).isEqualTo(shippingOption.getCode());
		assertThat(Optional.of(shippingServiceLevel.getCarrier())).isEqualTo(shippingOption.getCarrierCode());
		assertThat(Optional.of(shippingServiceLevel.getCarrier())).isEqualTo(shippingOption.getCarrierDisplayName());
		assertThat(shippingServiceLevel.getDisplayName(locale, false)).isEqualTo(shippingOption.getDisplayName(locale).orElse(null));
	}

	private ShippingServiceLevel getShippingServiceLevel(final String storeCode, final ShippingAddress shippingAddress) {
		final List<ShippingServiceLevel> shippingServiceLevels = shippingServiceLevelService
				.retrieveShippingServiceLevel(storeCode, shippingAddress);
		return shippingServiceLevels == null || shippingServiceLevels.isEmpty() ? null : shippingServiceLevels.get(0);
	}
}
