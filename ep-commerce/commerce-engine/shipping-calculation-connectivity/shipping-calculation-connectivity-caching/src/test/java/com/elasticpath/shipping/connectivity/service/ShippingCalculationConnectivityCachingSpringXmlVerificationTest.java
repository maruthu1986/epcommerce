/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.service;

import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.elasticpath.shipping.connectivity.commons.constants.ShippingContextIdNames;

/**
 * Test the validity of the Spring XML files.
 */
public class ShippingCalculationConnectivityCachingSpringXmlVerificationTest {

	private ConfigurableApplicationContext context;

	@Test(expected = org.springframework.beans.factory.NoSuchBeanDefinitionException.class)
	public void testLoadServiceWithDisableShippingCalculationConnectivityCachingProfile() {

		initContextWithProfile("disable-shipping-calculation-connectivity-caching");

		context.getBean(ShippingContextIdNames.SHIPPING_CALCULATION_SERVICE);

	}

	@Test
	public void testLoadServiceWithDefaultProfile() {

		initContextWithProfile("");

		context.getBean(ShippingContextIdNames.SHIPPING_CALCULATION_SERVICE);

	}

	@Test
	public void testLoadServiceWithOtherProfile() {

		initContextWithProfile("otherTestProfile");

		context.getBean(ShippingContextIdNames.SHIPPING_CALCULATION_SERVICE);

	}

	private void initContextWithProfile(final String profileName) {
		System.setProperty("spring.profiles.active", profileName);
		context = new ClassPathXmlApplicationContext("classpath:spring/shipping-calculation-connectivity-caching-spring-verification.xml");
	}

}
