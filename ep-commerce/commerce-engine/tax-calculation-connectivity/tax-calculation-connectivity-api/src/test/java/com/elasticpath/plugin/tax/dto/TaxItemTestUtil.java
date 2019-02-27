/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.plugin.tax.dto;

import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxAddress;

/**
 * Utility class for Tax tests.
 */
public final class TaxItemTestUtil {

	private TaxItemTestUtil() {
		//static class
	}

	/**
	 * Create a tax address.
	 *
	 * @param street1 street1
	 * @param street2 street2
	 * @param city city
	 * @param subCountry state
	 * @param zipCode zip code
	 * @param country country
	 * @return a new TaxAddress
	 */
	public static TaxAddress createTaxAddress(final String street1, final String street2, final String city,
		final String subCountry, final String zipCode, final String country) {

		MutableTaxAddress address = new MutableTaxAddress();
		address.setStreet1(street1);
		address.setStreet2(street2);
		address.setCity(city);
		address.setSubCountry(subCountry);
		address.setZipOrPostalCode(zipCode);
		address.setCountry(country);

		return address;
	}
}
