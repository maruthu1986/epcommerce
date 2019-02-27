/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.integration.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;
import com.elasticpath.shipping.connectivity.service.cache.impl.CachingShippingCalculationServiceImpl;

/**
 * Tests the bean wiring against the profile which disable shipping calculation connectivity caching.
 */
@ActiveProfiles("disable-shipping-calculation-connectivity-caching")
public class ShippingCalculationConnectivityBeanWiringWithDisableConnectivityCachingPluginITest
		extends ShippingCalculationConnectivityBeanWiringITest {

	@Test
	@Override
	public void testShippingCalculationService() {

		// currently, shipping calculation service has been its original proxy version.
		assertThat(shippingCalculationService).isNotInstanceOf(CachingShippingCalculationServiceImpl.class);
		assertThat(shippingCalculationService).isInstanceOf(ShippingCalculationService.class);

	}

}
