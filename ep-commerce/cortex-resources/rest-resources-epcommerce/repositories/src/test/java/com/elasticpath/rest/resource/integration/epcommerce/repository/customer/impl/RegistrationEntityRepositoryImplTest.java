/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.definition.registrations.RegistrationEntity;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.FormEntityToCustomerEnhancer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.customer.CustomerRegistrationService;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationEntityRepositoryImplTest {

	private static final String TEST_CUSTOMER_GUID = "testCustomerGuid";

	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private ExceptionTransformer exceptionTransformer;
	@Mock
	private CustomerRegistrationService customerRegistrationService;
	@Mock
	private FormEntityToCustomerEnhancer formEntityToCustomerEnhancer;
	@Mock
	private Customer customer;
	@Mock
	private RegistrationEntity registrationEntity;

	private final ProfileIdentifier profileIdentifier = ProfileIdentifier.builder()
			.withProfileId(StringIdentifier.of(TEST_CUSTOMER_GUID))
			.withScope(StringIdentifier.of("scope"))
			.build();

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapterImpl;

	@InjectMocks
	private RegistrationEntityRepositoryImpl<RegistrationEntity, ProfileIdentifier> registrationEntityRepository;

	@Before
	public void initialize() {
		registrationEntityRepository.setReactiveAdapter(reactiveAdapterImpl);
	}

	@Test
	public void testRegisterCustomer() {
		when(customerRepository.getCustomer(TEST_CUSTOMER_GUID)).thenReturn(Single.just(customer));
		when(formEntityToCustomerEnhancer.registrationEntityToCustomer(registrationEntity, customer)).thenReturn(customer);
		when(customerRegistrationService.registerCustomer(customer)).thenReturn(customer);

		registrationEntityRepository.update(registrationEntity, profileIdentifier)
				.test()
				.assertNoErrors();
		verify(customerRegistrationService).registerCustomer(customer);
	}

	@Test
	public void validationFailureShouldThrowException() {
		when(customerRepository.getCustomer(TEST_CUSTOMER_GUID)).thenReturn(Single.just(customer));
		when(formEntityToCustomerEnhancer.registrationEntityToCustomer(registrationEntity, customer)).thenReturn(customer);

		UserIdExistException userIdExistException = mock(UserIdExistException.class);
		when(customerRegistrationService.registerCustomer(customer)).thenThrow(userIdExistException);
		when(exceptionTransformer.getResourceOperationFailure(userIdExistException)).thenReturn(ResourceOperationFailure.stateFailure());

		registrationEntityRepository.update(registrationEntity, profileIdentifier)
				.test()
				.assertError(ResourceOperationFailure.class);
	}

}
