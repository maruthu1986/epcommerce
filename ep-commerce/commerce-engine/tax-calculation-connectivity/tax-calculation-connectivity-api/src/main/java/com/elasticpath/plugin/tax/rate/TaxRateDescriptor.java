/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate;

import java.math.BigDecimal;

import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.rate.dto.AppliedTaxValue;

/**
 * Tax rate descriptor with ability to be applied to an item.
 */
public interface TaxRateDescriptor {

	/**
	 * The value of this tax rate.
	 * 
	 * @return the value
	 */
	BigDecimal getTaxRateValue();

	/**
	 * Applies this tax rate on the given taxed item.
	 * 
	 * @param item the item to apply on
	 * @return the tax amount based on the given value (price)
	 */
	AppliedTaxValue applyTo(TaxedItem item);

	/**
	 * Gets the name associated with this tax rate.
	 * 
	 * @return the tax rate name
	 */
	String getTaxRateName();
	
	/**
	 * Gets the tax jurisdiction for this tax rate.
     *
	 * @return the tax jurisdiction
	 */
	String getTaxJurisdiction();

	/**
     * Gets the tax region for this tax rate.
	 *
	 * @return the tax region
	 */
	String getTaxRegion();

}