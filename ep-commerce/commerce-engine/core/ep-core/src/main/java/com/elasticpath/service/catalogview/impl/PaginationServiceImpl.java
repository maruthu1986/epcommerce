/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalogview.impl;

import com.google.common.base.MoreObjects;

import com.elasticpath.service.catalogview.PaginationService;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Paginates. 
  */
public class PaginationServiceImpl implements PaginationService {
	private static final int DEFAULT_PAGINATION_VALUE = 20;

	private SettingValueProvider<Integer> numberOfItemsPerPageSettingProvider;

	/**
	 * Calculate last page number.
	 * @param numberOfResults are the number of results returned
	 * @param storeCode Store code.
	 * @return the last page number
	 */
	@Override
	public int getLastPageNumber(final int numberOfResults,
								 final String storeCode) {
		
		int paginationNumber = getNumberOfItemsPerPage(storeCode);
		
		return getLastPageNumber(paginationNumber, numberOfResults);
	}

	/**
	 * Gets last page number.
	 * 
	 * @param itemsPerPage itemsPerPage
	 * @param numberOfItems numberOfItems
	 * @return last page number.
	 */
	int getLastPageNumber(final int itemsPerPage, final int numberOfItems) {
		int maxNumPages = numberOfItems / itemsPerPage;
		
		if (numberOfItems % itemsPerPage != 0) {
			maxNumPages += 1;
		}
		return maxNumPages;
	}

	/**
	 * Number of items per page.
	 * @param storeCode Store code.
	 * @return number of items per page.
	 */
	@Override
	public int getNumberOfItemsPerPage(final String storeCode) {
		final Integer configuredPaginationValue = getNumberOfItemsPerPageSettingProvider().get(storeCode);

		return MoreObjects.firstNonNull(configuredPaginationValue, DEFAULT_PAGINATION_VALUE);
	}

	protected SettingValueProvider<Integer> getNumberOfItemsPerPageSettingProvider() {
		return numberOfItemsPerPageSettingProvider;
	}

	public void setNumberOfItemsPerPageSettingProvider(final SettingValueProvider<Integer> numberOfItemsPerPageSettingProvider) {
		this.numberOfItemsPerPageSettingProvider = numberOfItemsPerPageSettingProvider;
	}

}
