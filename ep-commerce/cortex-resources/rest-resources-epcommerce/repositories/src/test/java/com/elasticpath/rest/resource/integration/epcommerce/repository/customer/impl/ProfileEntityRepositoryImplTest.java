package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

@RunWith(MockitoJUnitRunner.class)
public class ProfileEntityRepositoryImplTest {

	private ProfileEntity profileEntity;

	private ProfileIdentifier profileIdentifier;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private Single<Customer> customer;


	@InjectMocks
	private ProfileEntityRepositoryImpl repository;

	@Before
	public void setup() {
		profileEntity = ProfileEntity.builder().withFamilyName("Sage").withFamilyName("Sau").build();
		profileIdentifier = ProfileIdentifier
				.builder()
				.withProfileId(StringIdentifier.of("my-id"))
				.withScope(StringIdentifier.of("my-store"))
				.build();
	}

	@Test
	public void shouldFailUpdateProfileWhenCustomerNotFound() {
		when(customerRepository.getCustomer(profileIdentifier.getProfileId().getValue()))
				.thenReturn(Single.error(ResourceOperationFailure.notFound()));
		repository.update(profileEntity, profileIdentifier)
				.test()
				.assertError(ResourceOperationFailure.class);
	}

	@Test
	public void shouldUpdateProfile() {
		when(customerRepository.getCustomer(profileIdentifier.getProfileId().getValue())).thenReturn(customer);
		repository.update(profileEntity, profileIdentifier)
				.test()
				.assertNoErrors();
	}

	@Test
	public void shouldFailGetProfileWhenCustomerNotFoundError() {
		when(customerRepository.getCustomer(profileIdentifier.getProfileId().getValue()))
				.thenReturn(Single.error(ResourceOperationFailure.notFound()));
		repository.findOne(profileIdentifier)
				.test()
				.assertError(ResourceOperationFailure.class);
	}

	@Test
	public void shouldGetProfile() {
		when(customerRepository.getCustomer(profileIdentifier.getProfileId().getValue())).thenReturn(customer);
		repository.findOne(profileIdentifier)
				.test()
				.assertNoErrors();
	}
}
