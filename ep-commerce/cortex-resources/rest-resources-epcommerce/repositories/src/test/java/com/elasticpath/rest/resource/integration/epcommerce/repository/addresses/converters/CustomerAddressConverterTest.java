/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;

/**
 * Test for {@link CustomerAddressConverter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerAddressConverterTest {

	private static final String ADDRESS_ID = "addressId";
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Smith";
	private static final String STREET_ADDRESS = "123 Main St";
	private static final String EXTENDED_ADDRESS = "1st Ave";
	private static final String REGIONS = "BC";
	private static final String POSTAL_CODE = "V4H 2K5";
	private static final String LOCALITY = "Vancouver";
	private static final String COUNTRY = "CA";
	private static final String PHONE_NUMBER = "555-555-5555";
	private static final String ORGANIZATION = "COMPANY INC";

	@Mock
	private BeanFactory coreBeanFactory;

	@InjectMocks
	private CustomerAddressConverter converter;

	@Test
	public void shouldHaveAllCustomerAddressDetails() {
		when(coreBeanFactory.getBean(ContextIdNames.CUSTOMER_ADDRESS)).thenReturn(new CustomerAddressImpl());
		AddressEntity addressEntity = AddressEntity.builder()
				.withAddressId(ADDRESS_ID)
				.withName(getNameEntity())
				.withAddress(getAddressDetailEntity())
				.build();

		CustomerAddress customerAddress = converter.convert(addressEntity);

		assertThat(customerAddress.getStreet1()).isEqualTo(STREET_ADDRESS);
		assertThat(customerAddress.getStreet2()).isEqualTo(EXTENDED_ADDRESS);
		assertThat(customerAddress.getCity()).isEqualTo(LOCALITY);
		assertThat(customerAddress.getSubCountry()).isEqualTo(REGIONS);
		assertThat(customerAddress.getCountry()).isEqualTo(COUNTRY);
		assertThat(customerAddress.getZipOrPostalCode()).isEqualTo(POSTAL_CODE);
		assertThat(customerAddress.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
		assertThat(customerAddress.getOrganization()).isEqualTo(ORGANIZATION);
		assertThat(customerAddress.getFirstName()).isEqualTo(FIRST_NAME);
		assertThat(customerAddress.getLastName()).isEqualTo(LAST_NAME);
	}

	@Test
	public void shouldHaveNoAddressDetailsGivenNullAddressDetailEntity() {
		when(coreBeanFactory.getBean(ContextIdNames.CUSTOMER_ADDRESS)).thenReturn(new CustomerAddressImpl());
		AddressEntity addressEntity = AddressEntity.builder()
				.withAddressId(ADDRESS_ID)
				.withName(getNameEntity())
				.build();

		CustomerAddress customerAddress = converter.convert(addressEntity);

		assertThat(customerAddress.getStreet1()).isNull();
		assertThat(customerAddress.getStreet2()).isNull();
		assertThat(customerAddress.getCity()).isNull();
		assertThat(customerAddress.getSubCountry()).isNull();
		assertThat(customerAddress.getCountry()).isNull();
		assertThat(customerAddress.getZipOrPostalCode()).isNull();
		assertThat(customerAddress.getPhoneNumber()).isNull();
		assertThat(customerAddress.getOrganization()).isNull();
		assertThat(customerAddress.getFirstName()).isEqualTo(FIRST_NAME);
		assertThat(customerAddress.getLastName()).isEqualTo(LAST_NAME);
	}

	@Test
	public void shouldHaveNoNamesGivenNullNameEntity() {
		when(coreBeanFactory.getBean(ContextIdNames.CUSTOMER_ADDRESS)).thenReturn(new CustomerAddressImpl());
		AddressEntity addressEntity = AddressEntity.builder()
				.withAddressId(ADDRESS_ID)
				.withAddress(getAddressDetailEntity())
				.build();

		CustomerAddress customerAddress = converter.convert(addressEntity);

		assertThat(customerAddress.getStreet1()).isEqualTo(STREET_ADDRESS);
		assertThat(customerAddress.getStreet2()).isEqualTo(EXTENDED_ADDRESS);
		assertThat(customerAddress.getCity()).isEqualTo(LOCALITY);
		assertThat(customerAddress.getSubCountry()).isEqualTo(REGIONS);
		assertThat(customerAddress.getCountry()).isEqualTo(COUNTRY);
		assertThat(customerAddress.getZipOrPostalCode()).isEqualTo(POSTAL_CODE);
		assertThat(customerAddress.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
		assertThat(customerAddress.getOrganization()).isEqualTo(ORGANIZATION);
		assertThat(customerAddress.getFirstName()).isNull();
		assertThat(customerAddress.getLastName()).isNull();
	}

	private AddressDetailEntity getAddressDetailEntity() {
		return AddressDetailEntity.builder()
				.withStreetAddress(STREET_ADDRESS)
				.withExtendedAddress(EXTENDED_ADDRESS)
				.withRegion(REGIONS)
				.withPostalCode(POSTAL_CODE)
				.withLocality(LOCALITY)
				.withCountryName(COUNTRY)
				.withPhoneNumber(PHONE_NUMBER)
				.withOrganization(ORGANIZATION)
				.build();
	}

	private NameEntity getNameEntity() {
		return NameEntity.builder()
				.withGivenName(FIRST_NAME)
				.withFamilyName(LAST_NAME)
				.build();
	}
}