/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.repositories;

import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.definition.emails.EmailIdentifier;
import com.elasticpath.rest.definition.orders.EmailInfoIdentifier;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.common.authentication.AuthenticationConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Test for {@link EmailsLinkRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailsLinkRepositoryImplTest {

	private static final String USER_ID = "userId";
	private static final String VALID_EMAIL = "valid@valid.com";

	@Mock
	private Customer customer;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@InjectMocks
	private EmailsLinkRepositoryImpl<EmailInfoIdentifier, EmailIdentifier> repository;

	@Test
	public void shouldReturnEmailWhenItExistsAndValid() {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(customerRepository.getCustomer(USER_ID)).thenReturn(Single.just(customer));
		when(customer.getEmail()).thenReturn(VALID_EMAIL);

		repository.getElements(getEmailInfoIdentifier())
				.test()
				.assertNoErrors()
				.assertValue(emailIdentifier -> emailIdentifier.getEmailId().getValue().equals(VALID_EMAIL));
	}

	@Test
	public void shouldNotReturnEmailWhenAnonymousUser() {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(customerRepository.getCustomer(USER_ID)).thenReturn(Single.just(customer));
		when(customer.getEmail()).thenReturn(AuthenticationConstants.ANONYMOUS_USER_ID);

		repository.getElements(getEmailInfoIdentifier())
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void shouldNotReturnEmailWhenDoesNotExist() {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(customerRepository.getCustomer(USER_ID)).thenReturn(Single.just(customer));
		when(customer.getEmail()).thenReturn(null);

		repository.getElements(getEmailInfoIdentifier())
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	private EmailInfoIdentifier getEmailInfoIdentifier() {
		return EmailInfoIdentifier.builder()
				.withOrder(OrderIdentifier.builder()
						.withScope(StringIdentifier.of("scope"))
						.withOrderId(StringIdentifier.of("orderId"))
						.build())
				.build();
	}

}