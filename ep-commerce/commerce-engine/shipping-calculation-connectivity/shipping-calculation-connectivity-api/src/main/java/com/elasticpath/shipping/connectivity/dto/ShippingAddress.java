/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto;

/**
 * Interface defining address information for shipping calculation.
 */
public interface ShippingAddress {

	/**
	 * Gets guid of the shippable address.
	 *
	 * @return guid.
	 */
	String getGuid();

	/**
	 * Gets the first street name.
	 *
	 * @return the first street name.
	 */
	String getStreet1();

	/**
	 * Gets the second street name.
	 *
	 * @return the second street name.
	 */
	String getStreet2();

	/**
	 * Gets the city.
	 *
	 * @return the city.
	 */
	String getCity();

	/**
	 * Gets the sub country. (state, province, or other division of the country)
	 *
	 * @return the sub country.
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

}
