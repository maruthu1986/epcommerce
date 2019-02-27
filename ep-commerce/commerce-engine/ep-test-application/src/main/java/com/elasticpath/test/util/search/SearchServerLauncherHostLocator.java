/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.util.search;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.service.search.SearchHostLocator;

/**
 * Implementation of {@link SearchHostLocator} that delegates to a {@link Supplier}.
 */
public class SearchServerLauncherHostLocator implements SearchHostLocator {

	private static final Logger LOG = LoggerFactory.getLogger(SearchServerLauncherHostLocator.class);

	private Supplier<String> searchServerUrlSupplier;

	@Override
	public String getSearchHostLocation() {
		return searchServerUrlSupplier.get();
	}

	/**
	 * A fallback Supplier that returns null. Convenience method provided to facilitate Spring wiring.
	 *
	 * @return a Supplier that always returns null
	 */
	public static Supplier<String> getNullServerUrlSupplier() {
		LOG.warn("Returning null Search URL Supplier. If search functionality is required, override the searchServerUrlSupplier bean to supply the "
				+ "actual Search host URL.");
		return () -> null;
	}

	public Supplier<String> getSearchServerUrlSupplier() {
		return searchServerUrlSupplier;
	}

	public void setSearchServerUrlSupplier(final Supplier<String> searchServerUrlSupplier) {
		this.searchServerUrlSupplier = searchServerUrlSupplier;
	}

}
