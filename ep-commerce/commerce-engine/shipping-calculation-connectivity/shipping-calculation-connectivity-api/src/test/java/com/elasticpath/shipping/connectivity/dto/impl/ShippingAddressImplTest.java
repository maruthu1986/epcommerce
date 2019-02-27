/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Test cases for {@link ShippingAddressImpl}.
 */
public class ShippingAddressImplTest {

	private static final String COUNTRY = "testCountry";
	private static final String CITY = "testCity";
	private static final String SUB_COUNTRY = "testSubCountry";
	private static final String STREET_1 = "testStreet1";
	private static final String STREET_2 = "testStreet2";
	private static final String GUID = "testGuid";
	private static final String ZIP_OR_POSTAL_CODE = "testZipOrPostalCode";

	private ShippingAddressImpl shippingAddress;

	@Before
	public void setUp() {

		shippingAddress = buildEmpty();

	}

	@Test
	public void testCountry() {

		shippingAddress.setCountry(COUNTRY);
		assertThat(shippingAddress.getCountry()).isEqualTo(COUNTRY);

	}

	@Test
	public void testCity() {

		shippingAddress.setCity(CITY);
		assertThat(shippingAddress.getCity()).isEqualTo(CITY);

	}

	@Test
	public void testSubCountry() {

		shippingAddress.setSubCountry(SUB_COUNTRY);
		assertThat(shippingAddress.getSubCountry()).isEqualTo(SUB_COUNTRY);

	}

	@Test
	public void testStreet1() {

		shippingAddress.setStreet1(STREET_1);
		assertThat(shippingAddress.getStreet1()).isEqualTo(STREET_1);

	}

	@Test
	public void testStreet2() {

		shippingAddress.setStreet2(STREET_2);
		assertThat(shippingAddress.getStreet2()).isEqualTo(STREET_2);

	}

	@Test
	public void testGuid() {

		shippingAddress.setGuid(GUID);
		assertThat(shippingAddress.getGuid()).isEqualTo(GUID);

	}

	@Test
	public void testZipOrPostalCode() {

		shippingAddress.setZipOrPostalCode(ZIP_OR_POSTAL_CODE);
		assertThat(shippingAddress.getZipOrPostalCode()).isEqualTo(ZIP_OR_POSTAL_CODE);

	}

	@Test
	public void testToString() {

		final ShippingAddress shippingAddress = build(GUID, STREET_1, STREET_2, CITY, SUB_COUNTRY, COUNTRY, ZIP_OR_POSTAL_CODE);

		final String resultToString = shippingAddress.toString();

		assertThat(resultToString).contains(GUID, STREET_1, STREET_2, CITY, SUB_COUNTRY, COUNTRY, ZIP_OR_POSTAL_CODE);

	}

	@Test
	public void testEquals() {
		new EqualsTester()
				.addEqualityGroup(buildDefault(), buildEmpty(), shippingAddress)
				.addEqualityGroup(build("thatGuid", null, null, null, null, null, null))
				.addEqualityGroup(build(null, "thatStreet1", null, null, null, null, null))
				.addEqualityGroup(build(null, null, "thatStreet2", null, null, null, null))
				.addEqualityGroup(build(null, null, null, "thatCity", null, null, null))
				.addEqualityGroup(build(null, null, null, null, "thatSubCountry", null, null))
				.addEqualityGroup(build(null, null, null, null, null, "thatCountry", null))
				.addEqualityGroup(build(null, null, null, null, null, null, "thatZipOrPostalCode"))
				.testEquals();
	}

	@Test
	public void testCompareTo() {

		assertThat(shippingAddress.compareTo(shippingAddress)).isZero();
		assertThat(shippingAddress.compareTo(null)).isLessThan(0);

		testGreaterThan(build("thisGuid", null, null, null, null, null, null),
				build("thatGuid", null, null, null, null, null, null));
		testGreaterThan(build(null, "thisStreet1", null, null, null, null, null),
				build(null, "thatStreet1", null, null, null, null, null));
		testGreaterThan(build(null, null, "thisStreet2", null, null, null, null),
				build(null, null, "thatStreet2", null, null, null, null));
		testGreaterThan(build(null, null, null, "thisCity", null, null, null),
				build(null, null, null, "thatCity", null, null, null));
		testGreaterThan(build(null, null, null, null, "thisSubCountry", null, null),
				build(null, null, null, null, "thatSubCountry", null, null));
		testGreaterThan(build(null, null, null, null, null, "thisCountry", null),
				build(null, null, null, null, null, "thatCountry", null));
		testGreaterThan(build(null, null, null, null, null, null, "thisZipOrPostalCode"),
				build(null, null, null, null, null, null, "thatZipOrPostalCode"));
	}

	private void testGreaterThan(final ShippingAddressImpl thisShippingAddress, final ShippingAddressImpl thatShippingAddress) {
		assertThat(thisShippingAddress.compareTo(thatShippingAddress)).isGreaterThan(0);
	}

	private ShippingAddressImpl buildEmpty() {
		return build(null, null, null, null, null, null, null);
	}

	private ShippingAddressImpl buildDefault() {
		return build(null, null, null, null, null, null, null);
	}

	private ShippingAddressImpl build(final String guid, final String street1,
									  final String street2, final String city,
									  final String subCountry, final String country, final String zipOrPostalCode) {

		final ShippingAddressImpl shippingAddress = new ShippingAddressImpl();

		shippingAddress.setGuid(guid);
		shippingAddress.setStreet1(street1);
		shippingAddress.setStreet2(street2);
		shippingAddress.setCity(city);
		shippingAddress.setSubCountry(subCountry);
		shippingAddress.setCountry(country);
		shippingAddress.setZipOrPostalCode(zipOrPostalCode);

		return shippingAddress;
	}

}
