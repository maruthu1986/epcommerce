/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.plugin.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.elasticpath.plugin.shipping.impl.EpShippingCalculationPluginImpl;
import com.elasticpath.plugin.shipping.impl.UnpricedToPricedShippingCalculationPluginImpl;
import com.elasticpath.shipping.connectivity.spi.ShippingCalculationPlugin;

/**
 * Test the validity of the Spring XML files.
 */
public class ShippingCalculationPluginEpcommerceSpringXmlVerificationTest {


	private static final String SPRING_CONTEXT_FILE = "classpath:spring/shipping-calculation-plugin-epcommerce-spring-verification.xml";

	private ConfigurableApplicationContext context;

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Before
	public void setUp() {
		initContextWithProfile("");
	}

	@Test
	public void testServices() {

		assertThat(context.getBean("epShippingCalculationPlugin")).isNotNull().isInstanceOf(EpShippingCalculationPluginImpl.class);
		assertThat(context.getBean("unpricedToPricedShippingCalculationPlugin")).isNotNull()
				.isInstanceOf(UnpricedToPricedShippingCalculationPluginImpl.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPlugins() {

		final List<ShippingCalculationPlugin> unpricedShippingCalculationPluginList = (List<ShippingCalculationPlugin>) context.getBean(
				"unpricedShippingCalculationPluginList");

		assertThat(unpricedShippingCalculationPluginList).isNotNull();
		assertThat(unpricedShippingCalculationPluginList).isNotEmpty();


		final List<ShippingCalculationPlugin> pricedShippingCalculationPluginList = (List<ShippingCalculationPlugin>) context.getBean(
				"pricedShippingCalculationPluginList");

		assertThat(pricedShippingCalculationPluginList).isNotNull();
		assertThat(pricedShippingCalculationPluginList).isNotEmpty();

	}

	@Test
	public void testWithDisableProfile() {
		thrown.expect(org.springframework.beans.factory.NoSuchBeanDefinitionException.class);
		initContextWithProfile("disable-shipping-calculation-plugin-epcommerce");
		context.getBean("unpricedShippingCalculationPluginList");
		context.getBean("pricedShippingCalculationPluginList");

	}

	/**
	 * Other active profile should not affect this plugin default behaviour.
	 */
	@Test
	public void testWithOtherActiveProfile() {

		initContextWithProfile("otherActiveProfile");
		assertThat(context.getBean("unpricedShippingCalculationPluginList")).isNotNull();
		assertThat(context.getBean("pricedShippingCalculationPluginList")).isNotNull();

	}

	private void initContextWithProfile(final String profileName) {
		System.setProperty("spring.profiles.active", profileName);
		context = new ClassPathXmlApplicationContext(SPRING_CONTEXT_FILE);
	}

}
