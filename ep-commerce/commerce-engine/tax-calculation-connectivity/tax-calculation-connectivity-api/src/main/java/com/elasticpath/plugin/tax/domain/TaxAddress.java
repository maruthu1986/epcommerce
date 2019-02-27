/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

/**
 * Interface defining address information for tax calculations.
 */
public interface TaxAddress {

	/**
	 * Gets the first street name field.
	 * 
	 * @return the street name.
	 */
	String getStreet1();

	/**
	 * Gets the second street name field.
	 * 
	 * @return the street name.
	 */
	String getStreet2();

	/**
	 * Gets the city.
	 * 
	 * @return the city.
	 */
	String getCity();

	/**
	 * Gets the sub-country (state, province, or other division of the country).
	 * 
	 * @return the sub-country
	 */
	String getSubCountry();

	/**
	 * Gets the ZIP/postal code.
	 * 
	 * @return the ZIP/postal code.
	 */
	String getZipOrPostalCode();

	/**
	 * Gets the country.
	 * 
	 * @return the country.
	 */
	String getCountry();

	/**
	 * Gets the first name.
	 * 
	 * @return the first name.
	 */
	String getFirstName();

	/**
	 * Gets the last name .
	 * 
	 * @return the last name.
	 */
	String getLastName();

	/**
	 * Gets the phone number.
	 * 
	 * @return the phone number.
	 */
	String getPhoneNumber();

}