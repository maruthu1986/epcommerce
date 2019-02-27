/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate;

import java.util.List;

/**
 * Tax rate descriptor result.
 */
public interface TaxRateDescriptorResult {

	/**
	 * Determines whether or not this result is in a tax inclusive jurisdiction.
	 * 
	 * @return true if the result is in a tax inclusive juristiction
	 */
	boolean isTaxInclusive();

	/**
	 * Gets the list of tax rate descriptors.
	 *
	 * @return list of tax rate descriptors
	 */
	List<TaxRateDescriptor> getTaxRateDescriptors();

}