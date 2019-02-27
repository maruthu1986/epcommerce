/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.builder.ShippingAddressBuilder;

/**
 * Tests of {@link ShippingAddressTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingAddressTransformerImplTest {

	private static final String CITY = "testCity";
	private static final String COUNTRY = "testCountry";
	private static final String GUID = "testGuid";
	private static final String STREET_1 = "testStreet1";
	private static final String STREET_2 = "testStreet2";
	private static final String SUB_COUNTRY = "testSubCountry";
	private static final String ZIP_OR_POSTAL_CODE = "testZipOrPostalCode";

	@Mock
	private ShippingAddressBuilder shippingAddressBuilder;

	@Mock
	private Address address;

	@Mock
	private ShippingAddress shippingAddress;

	@InjectMocks
	private ShippingAddressTransformerImpl objectUnderTest;

	@Before
	public void setUp() {
		objectUnderTest.setShippingAddressBuilderSupplier(() -> shippingAddressBuilder);

		when(address.getCity()).thenReturn(CITY);
		when(address.getCountry()).thenReturn(COUNTRY);
		when(address.getGuid()).thenReturn(GUID);
		when(address.getStreet1()).thenReturn(STREET_1);
		when(address.getStreet2()).thenReturn(STREET_2);
		when(address.getSubCountry()).thenReturn(SUB_COUNTRY);
		when(address.getZipOrPostalCode()).thenReturn(ZIP_OR_POSTAL_CODE);

		when(shippingAddressBuilder.withCity(anyString())).thenReturn(shippingAddressBuilder);
		when(shippingAddressBuilder.withCountry(anyString())).thenReturn(shippingAddressBuilder);
		when(shippingAddressBuilder.withGuid(anyString())).thenReturn(shippingAddressBuilder);
		when(shippingAddressBuilder.withStreet1(anyString())).thenReturn(shippingAddressBuilder);
		when(shippingAddressBuilder.withStreet2(anyString())).thenReturn(shippingAddressBuilder);
		when(shippingAddressBuilder.withSubCountry(anyString())).thenReturn(shippingAddressBuilder);
		when(shippingAddressBuilder.withZipOrPostalCode(anyString())).thenReturn(shippingAddressBuilder);

		when(shippingAddressBuilder.build()).thenReturn(shippingAddress);
	}

	@Test
	public void verifyTransformerBuildsResultCorrectly() {
		final ShippingAddress actualShippingAddress = objectUnderTest.apply(address);

		verify(shippingAddressBuilder).withCity(CITY);
		verify(shippingAddressBuilder).withCountry(COUNTRY);
		verify(shippingAddressBuilder).withGuid(GUID);
		verify(shippingAddressBuilder).withStreet1(STREET_1);
		verify(shippingAddressBuilder).withStreet2(STREET_2);
		verify(shippingAddressBuilder).withSubCountry(SUB_COUNTRY);
		verify(shippingAddressBuilder).withZipOrPostalCode(ZIP_OR_POSTAL_CODE);

		verify(shippingAddressBuilder).build();

		assertThat(actualShippingAddress).isSameAs(shippingAddress);
	}

	@Test
	public void verifyAdaptNullInputAddressThrowsNPE() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.apply(null))
				.withMessage("Address is required");
	}
}
