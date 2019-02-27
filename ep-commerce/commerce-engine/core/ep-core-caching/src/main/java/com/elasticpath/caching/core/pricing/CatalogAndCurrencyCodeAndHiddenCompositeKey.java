/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.caching.core.pricing;

import java.util.Objects;

/**
 * Cache key for catalog code, currency code, and includeHidden.
 */
public class CatalogAndCurrencyCodeAndHiddenCompositeKey {

	private final String catalogCode;
	private final String currencyCode;
	private final boolean includeHidden;

	/**
	 * Constructor.
	 *
	 * @param catalogCode 	catalog code
	 * @param currencyCode	currency code
	 * @param includeHidden	include hidden
	 */
	CatalogAndCurrencyCodeAndHiddenCompositeKey(final String catalogCode, final String currencyCode, final boolean includeHidden) {
		this.catalogCode = catalogCode;
		this.currencyCode = currencyCode;
		this.includeHidden = includeHidden;
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
		CatalogAndCurrencyCodeAndHiddenCompositeKey that = (CatalogAndCurrencyCodeAndHiddenCompositeKey) other;

		return Objects.equals(catalogCode, that.catalogCode)
				&& Objects.equals(currencyCode, that.currencyCode)
				&& Objects.equals(includeHidden, that.includeHidden);
	}

	@Override
	public int hashCode() {
		return Objects.hash(catalogCode, currencyCode, includeHidden);
	}
}
