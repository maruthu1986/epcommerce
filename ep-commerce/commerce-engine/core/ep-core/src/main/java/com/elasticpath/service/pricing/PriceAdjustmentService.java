/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.pricing;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.pricing.PriceAdjustment;

/**
 * Service to retrieve Price Adjustments.
 */
public interface PriceAdjustmentService {

	/**
	 * Delete the PriceAdjustment.
	 *
	 * @param priceAdjustment to delete
	 * @throws EpServiceException in case of error
	 */
	void delete(PriceAdjustment priceAdjustment) throws EpServiceException;

	/**
	 * Find all the price adjustments defined on BundleConstituents in the given ProductBundle.
	 *
	 * @param plGuid price list guid
	 * @param bcList ProductBundle
	 *
	 * @return all PriceAdjustment found or empty collection.
	 *
	 * @deprecated Use {@link #findByPriceListAndBundleConstituentsAsMap(String, Collection)} instead.
	 */
	@Deprecated
	Collection<PriceAdjustment> findAllAdjustmentsOnBundle(String plGuid, Collection<String> bcList);

	/**
	 * Finds price adjustments for all of the product bundle's constituents (including nested bundles) in the given
	 * price list and returns them as a map, keyed by bundle constituent GUID.
	 *
	 * @param priceListGuid the price list GUID to use
	 * @param bcList the product bundle
	 * @return map of price adjustments, keyed by bundle constituent GUID
	 */
	Map<String, PriceAdjustment> findByPriceListAndBundleConstituentsAsMap(String priceListGuid, Collection<String> bcList);

	/**
	 * Find a PriceAdjustment by price list.
	 *
	 * @param plGuid pricelist guid
	 * @return list of PriceAdjustments found, empty list if none found
	 */
	List<PriceAdjustment> findByPriceList(String plGuid);
}
