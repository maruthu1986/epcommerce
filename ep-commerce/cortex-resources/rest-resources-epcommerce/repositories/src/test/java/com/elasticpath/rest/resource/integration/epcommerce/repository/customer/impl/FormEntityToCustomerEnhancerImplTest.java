/**
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.definition.registrations.RegistrationEntity;


/**
 * Tests the {@link FormEntityToCustomerEnhancerImpl class.
 */
@RunWith(MockitoJUnitRunner.class)
public class FormEntityToCustomerEnhancerImplTest {

	@Mock
	private Customer customer;

	@Mock
	private RegistrationEntity registrationEntity;

	@InjectMocks
	private FormEntityToCustomerEnhancerImpl formEntityToCustomerEnhancer;

	private static final String LAST_NAME = "Doe";
	private static final String FIST_NAME = "John";

	@Test
	public void registrationEntityToCustomerShouldReturnCustomerWithFirstNameLastNamePasswordEmail() {
		when(registrationEntity.getFamilyName()).thenReturn(LAST_NAME);
		when(registrationEntity.getGivenName()).thenReturn(FIST_NAME);
		when(registrationEntity.getPassword()).thenReturn("password");
		when(registrationEntity.getUsername()).thenReturn("john@doe.com");

		formEntityToCustomerEnhancer.registrationEntityToCustomer(registrationEntity, customer);

		verify(customer).setLastName(LAST_NAME);
		verify(customer).setFirstName(FIST_NAME);
		verify(customer).setClearTextPassword("password");
		verify(customer).setEmail("john@doe.com");
	}

}
