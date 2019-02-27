/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.service.catalogview.AvailabilityStrategy;

/**
 * Test the {@link FlagBasedAvailabilityStrategyImpl} behaves as expected.
 */
public class FlagBasedAvailabilityStrategyImplTest {

	private final AvailabilityStrategy strategy = new FlagBasedAvailabilityStrategyImpl();

	/**
	 * Test that when the isAvailable flag is false, a NOT_AVAILABLE availability is returned.
	 */
	@Test
	public void testNotAvailableWhenFlagIsFalse() {
		Product product = new ProductImpl();
		final Availability availability = strategy.getAvailability(product, false, false);
		assertEquals("The result should be NOT_AVAILABLE", Availability.NOT_AVAILABLE, availability);
	}

	/**
	 * Test that when the isAvailable flag is true, a null is returned, allowing other strategies to be called.
	 */
	@Test
	public void testNullWhenFlagIsTrue() {
		Product product = new ProductImpl();
		final Availability availability = strategy.getAvailability(product, true, false);
		assertNull("The result should be null", availability);

	}

}