/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.builder;

import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxAddress;

/**
 * Builder for {@link MutableTaxAddress}.
 */
public class TaxAddressBuilder {
	
	private final MutableTaxAddress taxAddress;
	
	/**
	 * Constructor.
	 */
	public TaxAddressBuilder() {
		taxAddress = new MutableTaxAddress();
	}
	
	/**
	 * Gets a new builder.
	 * 
	 * @return a new builder
	 */
	public static TaxAddressBuilder newBuilder() {
		return new TaxAddressBuilder();
	}
	
	/**
	 * Gets the instance built by the builder.
	 * 
	 * @return the built instance
	 */
	public TaxAddress build() {
		return taxAddress;
	}
	
	/**
	 * Sets street1.
	 * 
	 * @param street1 the given street1
	 * @return the builder
	 */
	public TaxAddressBuilder withStreet1(final String street1) {
		taxAddress.setStreet1(street1);
		return this;
	}
	
	/**
	 * Sets street2.
	 * 
	 * @param street2 the given street2
	 * @return the builder
	 */
	public TaxAddressBuilder withStreet2(final String street2) {
		taxAddress.setStreet2(street2);
		return this;
	}
	
	/**
	 * Sets the city.
	 * 
	 * @param city the given city
	 * @return the builder
	 */
	public TaxAddressBuilder withCity(final String city) {
		taxAddress.setCity(city);
		return this;
	}
	
	/**
	 * Sets the subCountry.
	 * 
	 * @param subCountry the given subCountry
	 * @return the builder
	 */
	public TaxAddressBuilder withSubCountry(final String subCountry) {
		taxAddress.setSubCountry(subCountry);
		return this;
	}
	
	/**
	 * Sets the country.
	 * 
	 * @param country the given country
	 * @return the builder
	 */
	public TaxAddressBuilder withCountry(final String country) {
		taxAddress.setCountry(country);
		return this;
	}

	/**
	 * Sets the zipOrPostalCode.
	 * 
	 * @param zipOrPostalCode the given zipOrPostalCode
	 * @return the builder
	 */
	public TaxAddressBuilder withZipOrPostalCode(final String zipOrPostalCode) {
		taxAddress.setZipOrPostalCode(zipOrPostalCode);
		return this;
	}
	
}
