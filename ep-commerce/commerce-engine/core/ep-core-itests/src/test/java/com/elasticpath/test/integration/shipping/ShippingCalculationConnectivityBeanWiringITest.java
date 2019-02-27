/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.integration.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.annotation.Resource;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import com.elasticpath.plugin.shipping.impl.EpShippingCalculationPluginImpl;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shipping.impl.ShippingOptionServiceImpl;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;
import com.elasticpath.shipping.connectivity.service.cache.impl.CachingShippingCalculationServiceImpl;
import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;
import com.elasticpath.shipping.connectivity.service.selector.PricedShippingCalculationPluginSelector;
import com.elasticpath.shipping.connectivity.service.selector.UnpricedShippingCalculationPluginSelector;
import com.elasticpath.shipping.connectivity.service.selector.impl.StaticPricedShippingCalculationPluginSelectorImpl;
import com.elasticpath.shipping.connectivity.service.selector.impl.StaticUnpricedShippingCalculationPluginSelectorImpl;
import com.elasticpath.test.integration.BasicSpringContextTest;

/**
 * Tests the overall shipping calculation connectivity bean wiring with overriding.
 */
public class ShippingCalculationConnectivityBeanWiringITest extends BasicSpringContextTest {

	@Resource
	private ShippingOptionService shippingOptionService;

	@Resource(name = "shippingCalculationService")
	ShippingCalculationService shippingCalculationService;

	@Resource(name = "pricedShippingCalculationPluginSelector")
	private PricedShippingCalculationPluginSelector pricedShippingCalculationPluginSelector;

	@Resource(name = "unpricedShippingCalculationPluginSelector")
	private UnpricedShippingCalculationPluginSelector unpricedShippingCalculationPluginSelector;

	@Resource(name = "pricedShippingCalculationPluginList")
	List<ShippingCalculationPlugin> pricedShippingCalculationPluginList;

	@Resource(name = "unpricedShippingCalculationPluginList")
	List<ShippingCalculationPlugin> unpricedShippingCalculationPluginList;

	@Test
	public void testShippingOptionService() {

		assertThat(shippingOptionService).isInstanceOf(ShippingOptionServiceImpl.class);

	}

	@Test
	public void testShippingCalculationService() {

		assertThat(shippingCalculationService).isInstanceOf(CachingShippingCalculationServiceImpl.class);

	}

	@Test
	public void testShippingCalculationProviderSelector() {

		assertThat(pricedShippingCalculationPluginSelector).isInstanceOf(StaticPricedShippingCalculationPluginSelectorImpl.class);
		assertThat(unpricedShippingCalculationPluginSelector).isInstanceOf(StaticUnpricedShippingCalculationPluginSelectorImpl.class);

	}

	@Test
	public void testShippingCalculationPlugin() {

		final SoftAssertions softAssertions = new SoftAssertions();

		softAssertions.assertThat(pricedShippingCalculationPluginList).isNotEmpty();
		softAssertions.assertThat(pricedShippingCalculationPluginList.get(0)).isNotNull();
		softAssertions.assertThat(pricedShippingCalculationPluginList.get(0)).isInstanceOf(EpShippingCalculationPluginImpl.class);
		softAssertions.assertThat(pricedShippingCalculationPluginList.get(0).getName()).isEqualTo("epShippingCalculationPlugin");

		softAssertions.assertThat(unpricedShippingCalculationPluginList).isNotEmpty();
		softAssertions.assertThat(unpricedShippingCalculationPluginList.get(0)).isNotNull();
		softAssertions.assertThat(unpricedShippingCalculationPluginList.get(0)).isInstanceOf(EpShippingCalculationPluginImpl.class);
		softAssertions.assertThat(unpricedShippingCalculationPluginList.get(0).getName()).isEqualTo("epShippingCalculationPlugin");

		softAssertions.assertAll();

	}

}
