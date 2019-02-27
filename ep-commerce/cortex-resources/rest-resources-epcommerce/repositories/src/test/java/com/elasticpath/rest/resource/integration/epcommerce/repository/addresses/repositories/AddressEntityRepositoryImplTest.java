/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.repositories;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.CartOrdersDefaultAddressPopulator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.validator.AddressValidator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

/**
 * Test for {@link AddressEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressEntityRepositoryImplTest {

	private static final String NO_VALID_ADDRESS_FIELDS = "No valid address fields specified.";

	private static final String STORE_CODE = "scope";
	private static final String CUSTOMER_ID = "customerId";
	private static final String ADDRESS_ID = "addressId";
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Smith";
	private static final String STREET_ADDRESS = "123 Main St";
	private static final String EXTENDED_ADDRESS = "1st Ave";
	private static final String REGIONS = "BC";
	private static final String POSTAL_CODE = "V4H 2K5";
	private static final String LOCALITY = "Vancouver";
	private static final String COUNTRY = "CA";

	@Mock
	private CustomerAddress customerAddress;

	@Mock
	private Customer customer;

	@Mock
	private CustomerAddress existingCustomerAddress;

	@Mock
	private CustomerAddress newCustomerAddress;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private ConversionService conversionService;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Mock
	private AddressValidator addressValidator;

	@Mock
	private CartOrdersDefaultAddressPopulator addressPopulator;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	@InjectMocks
	private AddressEntityRepositoryImpl<AddressEntity, AddressIdentifier> repository;

	@Before
	public void setUp() {
		repository.setReactiveAdapter(reactiveAdapter);
	}

	@Test
	public void shouldCreateNewCustomerAddressWhenItDoesNotExist() {
		AddressEntity addressEntity = getAddressEntity();
		when(addressValidator.validate(addressEntity)).thenReturn(Completable.complete());
		when(conversionService.convert(addressEntity, CustomerAddress.class)).thenReturn(customerAddress);
		when(resourceOperationContext.getUserIdentifier()).thenReturn(CUSTOMER_ID);
		when(customerRepository.getCustomer(CUSTOMER_ID)).thenReturn(Single.just(customer));
		when(customer.getAddresses()).thenReturn(Collections.emptyList());

		when(customerRepository.addAddress(customer, customerAddress)).thenReturn(Single.just(customer));
		when(customerRepository.update(customer)).thenReturn(Single.just(customer));
		when(addressPopulator.updateAllCartOrdersAddresses(customer, customerAddress, STORE_CODE, true, true))
				.thenReturn(Completable.complete());

		when(customerAddress.getGuid()).thenReturn(ADDRESS_ID);
		when(customer.getAddressByGuid(ADDRESS_ID)).thenReturn(customerAddress);
		
		repository.submit(addressEntity, StringIdentifier.of(STORE_CODE))
				.test()
				.assertNoErrors();

		verify(customerRepository, times(1)).addAddress(customer, customerAddress);
	}

	@Test
	public void shouldNotCreateNewCustomerAddressWhenAddressIsInvalid() {
		AddressEntity addressEntity = getAddressEntity();
		when(addressValidator.validate(addressEntity))
				.thenReturn(Completable.error(ResourceOperationFailure.badRequestBody(NO_VALID_ADDRESS_FIELDS)));
		when(conversionService.convert(addressEntity, CustomerAddress.class)).thenReturn(customerAddress);
		when(resourceOperationContext.getUserIdentifier()).thenReturn(CUSTOMER_ID);
		when(customerRepository.getCustomer(CUSTOMER_ID)).thenReturn(Single.just(customer));

		repository.submit(addressEntity, StringIdentifier.of(STORE_CODE))
				.test()
				.assertError(ResourceOperationFailure.badRequestBody(NO_VALID_ADDRESS_FIELDS));

		verify(customerRepository, never()).addAddress(customer, customerAddress);
	}


	@Test
	public void shouldNotCreateNewCustomerAddressWhenItAlreadyExists() {
		AddressEntity addressEntity = getAddressEntity();
		when(addressValidator.validate(addressEntity)).thenReturn(Completable.complete());
		when(conversionService.convert(addressEntity, CustomerAddress.class)).thenReturn(customerAddress);
		when(resourceOperationContext.getUserIdentifier()).thenReturn(CUSTOMER_ID);
		when(customerRepository.getCustomer(CUSTOMER_ID)).thenReturn(Single.just(customer));
		when(customer.getAddresses()).thenReturn(Collections.singletonList(customerAddress));
		when(customerAddress.getGuid()).thenReturn(ADDRESS_ID);

		repository.submit(addressEntity, StringIdentifier.of(STORE_CODE))
				.test()
				.assertNoErrors();

		verify(customerRepository, never()).addAddress(customer, customerAddress);
	}

	@Test
	public void shouldAddPreferredBillingAndShippingAddress() {
		when(addressPopulator.updateAllCartOrdersAddresses(customer, newCustomerAddress, STORE_CODE, true, true))
				.thenReturn(Completable.complete());
		when(customerRepository.update(customer)).thenReturn(Single.just(customer));

		repository.addCustomerPreferredAddress(STORE_CODE, customer, newCustomerAddress)
				.test()
				.assertNoErrors();

		verify(customer, times(1)).setPreferredBillingAddress(newCustomerAddress);
		verify(customer, times(1)).setPreferredShippingAddress(newCustomerAddress);
		verify(addressPopulator, times(1))
				.updateAllCartOrdersAddresses(customer, newCustomerAddress, STORE_CODE, true, true);
	}

	@Test
	public void shouldAddOnlyPreferredBillingAddressWhenShippingAddressExists() {
		when(customer.getPreferredShippingAddress()).thenReturn(existingCustomerAddress);
		when(addressPopulator.updateAllCartOrdersAddresses(customer, newCustomerAddress, STORE_CODE, true, false))
				.thenReturn(Completable.complete());
		when(customerRepository.update(customer)).thenReturn(Single.just(customer));

		repository.addCustomerPreferredAddress(STORE_CODE, customer, newCustomerAddress)
				.test()
				.assertNoErrors();

		verify(customer, times(1)).setPreferredBillingAddress(newCustomerAddress);
		verify(customer, never()).setPreferredShippingAddress(newCustomerAddress);
		verify(addressPopulator, times(1))
				.updateAllCartOrdersAddresses(customer, newCustomerAddress, STORE_CODE, true, false);
	}

	@Test
	public void shouldAddOnlyPreferredShippingAddressWhenBillingAddressExists() {
		when(customer.getPreferredBillingAddress()).thenReturn(existingCustomerAddress);
		when(addressPopulator.updateAllCartOrdersAddresses(customer, newCustomerAddress, STORE_CODE, false, true))
				.thenReturn(Completable.complete());
		when(customerRepository.update(customer)).thenReturn(Single.just(customer));

		repository.addCustomerPreferredAddress(STORE_CODE, customer, newCustomerAddress)
				.test()
				.assertNoErrors();

		verify(customer, never()).setPreferredBillingAddress(newCustomerAddress);
		verify(customer, times(1)).setPreferredShippingAddress(newCustomerAddress);
		verify(addressPopulator, times(1))
				.updateAllCartOrdersAddresses(customer, newCustomerAddress, STORE_CODE, false, true);
	}

	@Test
	public void shouldNotAddPreferredBillingAndShippingAddress() {
		when(customer.getPreferredBillingAddress()).thenReturn(existingCustomerAddress);
		when(customer.getPreferredShippingAddress()).thenReturn(existingCustomerAddress);
		when(customerRepository.update(customer)).thenReturn(Single.just(customer));

		repository.addCustomerPreferredAddress(STORE_CODE, customer, newCustomerAddress)
				.test()
				.assertNoErrors();

		verify(customer, never()).setPreferredBillingAddress(newCustomerAddress);
		verify(customer, never()).setPreferredShippingAddress(newCustomerAddress);
		verify(addressPopulator, never())
				.updateAllCartOrdersAddresses(any(Customer.class), any(CustomerAddress.class), anyString(), anyBoolean(), anyBoolean());
	}

	@Test
	public void shouldUpdateCustomerAddressWhenAddressChangeIsValid() {
		AddressEntity addressEntity = getAddressEntity();
		when(addressValidator.validate(addressEntity)).thenReturn(Completable.complete());
		when(resourceOperationContext.getUserIdentifier()).thenReturn(CUSTOMER_ID);
		when(customerRepository.getCustomer(CUSTOMER_ID)).thenReturn(Single.just(customer));
		when(customer.getAddressByGuid(ADDRESS_ID)).thenReturn(existingCustomerAddress);
		when(customerRepository.updateAddress(customer, existingCustomerAddress)).thenReturn(Completable.complete());

		repository.update(addressEntity, getAddressIdentifier())
				.test()
				.assertNoErrors();

		verify(customerRepository, times(1)).updateAddress(customer, existingCustomerAddress);
	}

	@Test
	public void shouldNotUpdateCustomerAddressWhenAddressChangeIsInvalid() {
		AddressEntity addressEntity = getAddressEntity();
		when(addressValidator.validate(addressEntity))
				.thenReturn(Completable.error(ResourceOperationFailure.badRequestBody(NO_VALID_ADDRESS_FIELDS)));
		when(resourceOperationContext.getUserIdentifier()).thenReturn(CUSTOMER_ID);
		when(customerRepository.getCustomer(CUSTOMER_ID)).thenReturn(Single.just(customer));

		repository.update(addressEntity, getAddressIdentifier())
				.test()
				.assertError(ResourceOperationFailure.badRequestBody(NO_VALID_ADDRESS_FIELDS));

		verify(customerRepository, never()).updateAddress(customer, existingCustomerAddress);
	}

	@Test
	public void shouldUpdateAllCustomerAddressDetails() {
		AddressEntity addressEntity = AddressEntity.builder()
				.withAddressId(ADDRESS_ID)
				.withName(getNameEntity())
				.withAddress(getAddressDetailEntity())
				.build();

		repository.updateCustomerAddress(addressEntity, newCustomerAddress);

		verify(newCustomerAddress, times(1)).setStreet1(STREET_ADDRESS);
		verify(newCustomerAddress, times(1)).setStreet2(EXTENDED_ADDRESS);
		verify(newCustomerAddress, times(1)).setCity(LOCALITY);
		verify(newCustomerAddress, times(1)).setSubCountry(REGIONS);
		verify(newCustomerAddress, times(1)).setCountry(COUNTRY);
		verify(newCustomerAddress, times(1)).setZipOrPostalCode(POSTAL_CODE);
		verify(newCustomerAddress, times(1)).setFirstName(FIRST_NAME);
		verify(newCustomerAddress, times(1)).setLastName(LAST_NAME);
	}

	@Test
	public void shouldNotUpdateAddressDetailsGivenNullAddressDetailEntity() {
		AddressEntity addressEntity = AddressEntity.builder()
				.withAddressId(ADDRESS_ID)
				.withName(getNameEntity())
				.build();

		repository.updateCustomerAddress(addressEntity, newCustomerAddress);

		verify(newCustomerAddress, never()).setStreet1(anyString());
		verify(newCustomerAddress, never()).setStreet2(anyString());
		verify(newCustomerAddress, never()).setCity(anyString());
		verify(newCustomerAddress, never()).setSubCountry(anyString());
		verify(newCustomerAddress, never()).setCountry(anyString());
		verify(newCustomerAddress, never()).setZipOrPostalCode(anyString());
		verify(newCustomerAddress, times(1)).setFirstName(FIRST_NAME);
		verify(newCustomerAddress, times(1)).setLastName(LAST_NAME);
	}

	@Test
	public void shouldNotUpdateNamesGivenNullNameEntity() {
		AddressEntity addressEntity = AddressEntity.builder()
				.withAddressId(ADDRESS_ID)
				.withAddress(getAddressDetailEntity())
				.build();

		repository.updateCustomerAddress(addressEntity, newCustomerAddress);

		verify(newCustomerAddress, times(1)).setStreet1(STREET_ADDRESS);
		verify(newCustomerAddress, times(1)).setStreet2(EXTENDED_ADDRESS);
		verify(newCustomerAddress, times(1)).setCity(LOCALITY);
		verify(newCustomerAddress, times(1)).setSubCountry(REGIONS);
		verify(newCustomerAddress, times(1)).setCountry(COUNTRY);
		verify(newCustomerAddress, times(1)).setZipOrPostalCode(POSTAL_CODE);
		verify(newCustomerAddress, never()).setFirstName(anyString());
		verify(newCustomerAddress, never()).setLastName(anyString());
	}

	private AddressIdentifier getAddressIdentifier() {
		return AddressIdentifier.builder()
				.withAddresses(AddressesIdentifier.builder()
						.withScope(StringIdentifier.of(STORE_CODE))
						.build())
				.withAddressId(StringIdentifier.of(ADDRESS_ID))
				.build();
	}

	private AddressEntity getAddressEntity() {
		return AddressEntity.builder()
				.withAddress(getAddressDetailEntity())
				.withName(getNameEntity())
				.withAddressId(ADDRESS_ID)
				.build();
	}

	private AddressDetailEntity getAddressDetailEntity() {
		return AddressDetailEntity.builder()
				.withStreetAddress(STREET_ADDRESS)
				.withExtendedAddress(EXTENDED_ADDRESS)
				.withRegion(REGIONS)
				.withPostalCode(POSTAL_CODE)
				.withLocality(LOCALITY)
				.withCountryName(COUNTRY)
				.build();
	}

	private NameEntity getNameEntity() {
		return NameEntity.builder()
				.withGivenName(FIRST_NAME)
				.withFamilyName(LAST_NAME)
				.build();
	}
}