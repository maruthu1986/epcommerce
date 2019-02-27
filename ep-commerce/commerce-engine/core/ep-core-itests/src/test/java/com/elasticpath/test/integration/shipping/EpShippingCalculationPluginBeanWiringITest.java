package com.elasticpath.test.integration.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.annotation.Resource;

import org.junit.Test;

import com.elasticpath.plugin.shipping.impl.EpShippingCalculationPluginImpl;
import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;
import com.elasticpath.test.integration.BasicSpringContextTest;

public class EpShippingCalculationPluginBeanWiringITest extends BasicSpringContextTest {


	@Resource(name = "epShippingCalculationPlugin")
	private ShippingCalculationPlugin shippingCalculationPlugin;

	@Resource(name = "epUnpricedShippingCalculationPluginList")
	private List<ShippingCalculationPlugin> epUnpricedShippingCalculationPluginList;

	@Resource(name = "epPricedShippingCalculationPluginList")
	private List<ShippingCalculationPlugin> epPricedShippingCalculationPluginList;

	@Test
	public void verifyBeans() {

		assertThat(shippingCalculationPlugin).isNotNull();
		assertThat(shippingCalculationPlugin.getClass().isAssignableFrom(EpShippingCalculationPluginImpl.class)).isTrue();

		assertThat(epUnpricedShippingCalculationPluginList).isNotEmpty();
		assertThat(epPricedShippingCalculationPluginList).isNotEmpty();

		assertThat(epUnpricedShippingCalculationPluginList.get(0).getName()).isEqualTo("epShippingCalculationPlugin");
		assertThat(epPricedShippingCalculationPluginList.get(0).getName()).isEqualTo("epShippingCalculationPlugin");

	}

}
