/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

/** Test cases for <code>ShippingCostCalculationParameterImpl</code>.*/
public class ShippingCostCalculationParameterImplTest {

	private static final String TEST_VALUE = "testValue";

	private ShippingCostCalculationParameterImpl shippingCostCalculationParameterImpl;


	/**
	 * Prepare for each test.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		this.shippingCostCalculationParameterImpl = new ShippingCostCalculationParameterImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl.getKey()'.
	 */
	@Test
	public void testGetSetKey() {
		final String testKey = "testKey";
		shippingCostCalculationParameterImpl.setKey(testKey);
		assertThat(shippingCostCalculationParameterImpl.getKey()).isEqualTo(testKey);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl.getValue()'.
	 */
	@Test
	public void testGetSetValue() {
		shippingCostCalculationParameterImpl.setValue(TEST_VALUE);
		assertThat(shippingCostCalculationParameterImpl.getValue()).isEqualTo(TEST_VALUE);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl.getValue()'.
	 */
	@Test
	public void testGetDisplayText() {
		// Test that value is returned if displayText is null
		shippingCostCalculationParameterImpl.setValue(TEST_VALUE);
		assertThat(shippingCostCalculationParameterImpl.getDisplayText()).isEqualTo(TEST_VALUE);

		final String displayText = "testText";
		shippingCostCalculationParameterImpl.setDisplayText(displayText);
		assertThat(shippingCostCalculationParameterImpl.getDisplayText()).isEqualTo(displayText);
	}

}

