/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.spi;

import java.io.Serializable;

import com.elasticpath.shipping.connectivity.spi.capability.ShippingCalculationCapability;

/**
 * Service Provider Interface for extension classes implementing shipping calculation plugins.
 */
public abstract class AbstractShippingCalculationPlugin implements ShippingCalculationPlugin, Serializable {
	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public <T extends ShippingCalculationCapability> T getCapability(final Class<T> capability) {
		if (hasCapability(capability)) {
			return capability.cast(this);
		}
		return null;
	}

	@Override
	public <T extends ShippingCalculationCapability> boolean hasCapability(final Class<T> capability) {
		return capability.isAssignableFrom(this.getClass());
	}

}
