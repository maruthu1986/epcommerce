/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.caching.core.pricing;

import java.util.Collection;
import java.util.Objects;

/**
 * A custom cache key consisting of pricelist GUID and a list of bundle constituents IDs.
 */
public class PriceListGuidAndBundleConstituentsKey {

	private final String priceListGuid;
	private final Collection<String> bundleConstituentsIds;

	/**
	 * Default constructor.
	 *
	 * @param priceListGuid price list gui
	 * @param bundleConstituentsIds bundle constituents' IDs
	 */
	PriceListGuidAndBundleConstituentsKey(final String priceListGuid, final Collection<String> bundleConstituentsIds) {
		this.priceListGuid = priceListGuid;
		this.bundleConstituentsIds = bundleConstituentsIds;
	}

	public String getPriceListGuid() {
		return priceListGuid;
	}

	public Collection<String> getBundleConstituentsIds() {
		return bundleConstituentsIds;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}

		if (this == other) {
			return true;
		}

		if (getClass() != other.getClass()) {
			return false;
		}
		PriceListGuidAndBundleConstituentsKey that = (PriceListGuidAndBundleConstituentsKey) other;

		return Objects.equals(priceListGuid, that.priceListGuid)
			&& Objects.equals(bundleConstituentsIds, that.bundleConstituentsIds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(priceListGuid, bundleConstituentsIds);
	}
}
