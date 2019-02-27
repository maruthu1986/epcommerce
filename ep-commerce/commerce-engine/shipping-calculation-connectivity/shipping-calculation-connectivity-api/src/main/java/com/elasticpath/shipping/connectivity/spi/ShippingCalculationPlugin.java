/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.spi;

import com.elasticpath.shipping.connectivity.spi.capability.ShippingCalculationCapability;

/**
 * Interface defining shipping calculation plugin api.
 */
public interface ShippingCalculationPlugin {

	/**
	 * Provides access to shipping provider capabilities not exposed through the base API.
	 *
	 * @param capability the requested capability
	 * @param <T>        a class or interface implementing {@link ShippingCalculationCapability}
	 * @return the capability requested, if available on this instance. Null otherwise.
	 */
	<T extends ShippingCalculationCapability> T getCapability(Class<T> capability);

	/**
	 * Provides checking on given shipping provider capabilities not exposed through the base API.
	 *
	 * @param capability the requested capability
	 * @param <T>        a class or interface implementing {@link ShippingCalculationCapability}
	 * @return true if has the requested capability
	 */
	<T extends ShippingCalculationCapability> boolean hasCapability(Class<T> capability);

	/**
	 * Gets name of shipping provider plugin.
	 *
	 * @return the plugin name.
	 */
	String getName();

}
