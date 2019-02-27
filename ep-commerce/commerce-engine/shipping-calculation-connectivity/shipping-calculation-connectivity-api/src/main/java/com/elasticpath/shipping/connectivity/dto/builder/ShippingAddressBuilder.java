/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder;

import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Interface defining builder of {@link ShippingAddress}.
 */
public interface ShippingAddressBuilder extends Builder<ShippingAddress, ShippingAddressBuilder> {

	/**
	 * Sets the guid of address.
	 *
	 * @param guid the guid of address.
	 * @return instance of impl
	 */
	ShippingAddressBuilder withGuid(String guid);

	/**
	 * Sets the street1 line.
	 *
	 * @param street1 the street1 line
	 * @return instance of impl
	 */
	ShippingAddressBuilder withStreet1(String street1);

	/**
	 * Sets the street2 line.
	 *
	 * @param street2 the street2 line
	 * @return instance of impl
	 */
	ShippingAddressBuilder withStreet2(String street2);

	/**
	 * Sets the city.
	 *
	 * @param city the city.
	 * @return instance of impl
	 */
	ShippingAddressBuilder withCity(String city);

	/**
	 * Sets the sub country.
	 *
	 * @param subCountry the sub country.
	 * @return instance of impl
	 */
	ShippingAddressBuilder withSubCountry(String subCountry);

	/**
	 * Sets the zip or postal code.
	 *
	 * @param zipOrPostalCode the zip or postal code
	 * @return instance of impl
	 */
	ShippingAddressBuilder withZipOrPostalCode(String zipOrPostalCode);

	/**
	 * Sets the country.
	 *
	 * @param country the country
	 * @return instance of impl
	 */
	ShippingAddressBuilder withCountry(String country);
}
