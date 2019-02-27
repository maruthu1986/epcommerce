/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.model;

import java.util.Collection;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * This model keeps track of search results for PriceListDescriptorDTOs.
 */
public interface PriceListManagerSearchResultsModel {

	/**
	 * Sets the search results.
	 * @param priceListDescriptors the PriceListDescriptors found as a result of a search.
	 */
	void setPriceListDescriptorSearchResults(
			Collection<PriceListDescriptorDTO> priceListDescriptors);

	/**
	 * @return the PriceListDescriptorDTOs found as a result of a search.
	 */
	Collection<PriceListDescriptorDTO> getPriceListDescriptorSearchResults();

}