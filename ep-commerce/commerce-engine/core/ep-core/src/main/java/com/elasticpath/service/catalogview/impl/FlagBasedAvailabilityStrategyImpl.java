/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalogview.impl;

import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalogview.AvailabilityStrategy;

/**
 * Availability strategy that returns a NOT_AVAILABLE availability if the availability flag is false.
 */
public class FlagBasedAvailabilityStrategyImpl implements AvailabilityStrategy {
	@Override
	public Availability getAvailability(final Product product, final boolean isAvailable, final boolean isDisplayable) {
		if (!isAvailable) {
			return Availability.NOT_AVAILABLE;
		}

		return null;
	}
}
