/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection.impl;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.plugin.tax.domain.TaxAddress;

/**
 * The tax calculation region. This implementation equates a tax region to a country, which is consistent with
 * current tax calculation practices.
 */
public class TaxRegion {

	private String country;
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(final String country) {
		this.country = country;
	}
	
	/**
	 * Determines whether or not this tax region matches a given tax address.
	 * 
	 * @param taxAddress the tax address
	 * @return true if taxAddress is a match for this region
	 */
	public boolean matches(final TaxAddress taxAddress) {
		return StringUtils.equalsIgnoreCase(taxAddress.getCountry(), getCountry());
	}
	
}
