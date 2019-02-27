/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.integration.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests bean wiring against the profile which disable shipping calculation plugin epcommerce.
 */
@ActiveProfiles("disable-shipping-calculation-plugin-epcommerce")
public class ShippingCalculationConnectivityBeanWiringWithDisableEpPluginITest
		extends ShippingCalculationConnectivityBeanWiringITest {

	@Test
	@Override
	public void testShippingCalculationPlugin() {

		// since default shipping calculation plugins are represented by NoOpProxyFactoryBean,
		// the reference of beans exists but are populated as null.
		assertThat(pricedShippingCalculationPluginList).isNotEmpty();
		assertThat(unpricedShippingCalculationPluginList).isNotEmpty();

		assertThat(pricedShippingCalculationPluginList.get(0)).isNull();
		assertThat(unpricedShippingCalculationPluginList.get(0)).isNull();

	}


}