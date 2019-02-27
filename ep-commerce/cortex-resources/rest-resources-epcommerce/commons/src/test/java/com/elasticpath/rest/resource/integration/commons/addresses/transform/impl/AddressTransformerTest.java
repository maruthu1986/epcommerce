/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.commons.addresses.transform.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.resource.integration.commons.addresses.transform.AddressTransformer;

/**
 * Tests for {@link AddressTransformer}.
 */
public class AddressTransformerTest {
	private static final String ADDRESS_GUID = "address_guid";
	private static final String ZIP_CODE = "zipCode";
	private static final String SUB_COUNTRY = "BC";
	private static final String COUNTRY = "CA";
	private static final String CITY = "city";
	private static final String STREET2 = "street2";
	private static final String STREET1 = "street1";
	private static final String LAST_NAME = "last_name";
	private static final String FIRST_NAME = "first_name";

	private final AddressTransformer classUnderTest = new AddressTransformerImpl();

	@Test(expected = NullPointerException.class)
	public void ensureNullAddressThrowsException() {
		classUnderTest.transformAddressToEntity(null);
	}

	@Test
	public void ensureAllFieldsAreCopied() {
		AddressDetailEntity expectedAddressEntity = AddressDetailEntity.builder()
				.withStreetAddress(STREET1)
				.withExtendedAddress(STREET2)
				.withLocality(CITY)
				.withCountryName(COUNTRY)
				.withRegion(SUB_COUNTRY)
				.withPostalCode(ZIP_CODE)
				.withPhoneNumber(null)
				.withOrganization(null)
				.build();
		NameEntity nameEntity = NameEntity.builder()
				.withGivenName(FIRST_NAME)
				.withFamilyName(LAST_NAME)
				.build();

		AddressEntity addressEntity = AddressEntity.builder()
				.withAddressId(ADDRESS_GUID)
				.withAddress(expectedAddressEntity)
				.withName(nameEntity)
				.build();

		Address address = new CustomerAddressImpl();
		address.setGuid(ADDRESS_GUID);
		address.setFirstName(FIRST_NAME);
		address.setLastName(LAST_NAME);
		address.setStreet1(STREET1);
		address.setStreet2(STREET2);
		address.setCity(CITY);
		address.setCountry(COUNTRY);
		address.setSubCountry(SUB_COUNTRY);
		address.setZipOrPostalCode(ZIP_CODE);

		AddressEntity actualAddressEntity = classUnderTest.transformAddressToEntity(address);
		assertEquals(addressEntity, actualAddressEntity);
	}
}
