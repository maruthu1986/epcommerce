/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

import java.math.BigDecimal;

/**
 * Interface representing the most granular level of tax calculations returned by a tax provider plugin.
 * A separate tax record is returned for each tax name (e.g. GST, PST) for each taxed item.
 */
public interface TaxRecord {

	/**
	 * Tax name that should be returned by TaxRecord implementations when queried for their name (see {@link #getTaxName()}) if it indicates that
	 * no tax rate has been matched (either because none are applicable or because the tax cannot yet be calculated may because of incomplete 
	 * information, ie no address for taxation has currently been supplied).
	 */
	String NO_TAX_RATE_TAX_NAME = "NO_TAX";
	
	/**
	 * Gets item's tax code.
	 * 
	 * @return the tax code
	 */
	String getTaxCode();
	
	/**
	 * Gets the tax name returned by the tax provider.
	 * 
	 * @return the tax name
	 */
	String getTaxName();
	
	/**
	 * Gets tax rate returned by the tax provider.
	 *
	 * @return the tax rate.
	 */
	BigDecimal getTaxRate();	
	
	/**
	 * Gets the tax jurisdiction.
	 *
	 * @return the tax jurisdiction
	 */
	String getTaxJurisdiction();
	
	/**
	 * Gets the tax region.
	 *
	 * @return the tax region
	 */
	String getTaxRegion();
	
	/**
	 * Gets the amount of calculated taxes.
     *
	 * @return the tax value
	 */
	BigDecimal getTaxValue();
	
	/**
     * Gets the name of the tax provider.
	 *
	 * @return the tax provider
	 */
	String getTaxProvider();

}
