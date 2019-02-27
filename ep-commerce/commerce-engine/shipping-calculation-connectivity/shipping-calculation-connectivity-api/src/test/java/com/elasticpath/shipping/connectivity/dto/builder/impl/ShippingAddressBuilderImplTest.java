/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.builder.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.shipping.connectivity.dto.ShippingAddress;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingAddressImpl;

/**
 * Test cases for {@link ShippingAddressBuilderImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingAddressBuilderImplTest {

	private static final String CITY = "testCity";
	private static final String COUNTRY = "testCountry";
	private static final String GUID = "testGuid";
	private static final String STREET_1 = "testStreet1";
	private static final String STREET_2 = "testStreet2";
	private static final String SUB_COUNTRY = "testSubCountry";
	private static final String ZIP_OR_POSTAL_CODE = "testZipOrPostalCode";

	private ShippingAddressBuilderImpl targetBuilder;


	@Mock
	private ShippingAddressImpl mockShippingAddress;

	@Mock
	private Supplier<ShippingAddressImpl> mockSupplier;

	@Before
	public void setUp() {

		targetBuilder = new ShippingAddressBuilderImpl();
		targetBuilder.setInstanceSupplier(mockSupplier);

		when(mockSupplier.get()).thenReturn(mockShippingAddress);

	}

	@Test
	public void testBuild() {

		targetBuilder.withCity(CITY);
		targetBuilder.withCountry(COUNTRY);
		targetBuilder.withGuid(GUID);
		targetBuilder.withStreet1(STREET_1);
		targetBuilder.withStreet2(STREET_2);
		targetBuilder.withSubCountry(SUB_COUNTRY);
		targetBuilder.withZipOrPostalCode(ZIP_OR_POSTAL_CODE);

		final ShippingAddress shippingAddress = targetBuilder.build();

		assertThat(shippingAddress).isNotNull();

		verify(mockShippingAddress).setCity(CITY);
		verify(mockShippingAddress).setCountry(COUNTRY);
		verify(mockShippingAddress).setGuid(GUID);
		verify(mockShippingAddress).setStreet1(STREET_1);
		verify(mockShippingAddress).setStreet2(STREET_2);
		verify(mockShippingAddress).setSubCountry(SUB_COUNTRY);
		verify(mockShippingAddress).setZipOrPostalCode(ZIP_OR_POSTAL_CODE);

	}

}
