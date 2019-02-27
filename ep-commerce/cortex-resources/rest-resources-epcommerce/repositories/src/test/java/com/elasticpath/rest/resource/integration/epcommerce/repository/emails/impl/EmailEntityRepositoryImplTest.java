/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.emails.impl;

import static org.mockito.Mockito.when;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.definition.emails.EmailIdentifier;
import com.elasticpath.rest.definition.emails.EmailsIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl.CustomerRepositoryImpl;

/**
 * Test for {@link EmailEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailEntityRepositoryImplTest {

	private static final String SCOPE = "SCOPE";
	private static final String EXPECTED_EMAIL = "Godzilla@lizard.com";
	private static final String USER_ID = "Godzilla";
	private static final String CUSTOMER_WAS_NOT_FOUND = "Customer was not found.";

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Mock
	private Customer customer;

	@Mock
	private CustomerRepositoryImpl customerRepository;

	@InjectMocks
	private EmailEntityRepositoryImpl<EmailEntity, EmailIdentifier> emailEntityRepository;

	@Before
	public void initialize() {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(customer.getEmail()).thenReturn(EXPECTED_EMAIL);
		emailEntityRepository.setResourceOperationContext(resourceOperationContext);
		emailEntityRepository.setCustomerRepository(customerRepository);
	}

	@Test
	public void findOneProducesCorrectCustomerWithValidEmailIdentifier() {
		when(customerRepository.getCustomer(USER_ID)).thenReturn(Single.just(customer));
		emailEntityRepository.findOne(getEmailIdentifier())
				.test()
				.assertNoErrors()
				.assertValue(emailEntity -> emailEntity.getEmail().equals(EXPECTED_EMAIL));
	}

	@Test
	public void findOneDoesNotCompleteAndThrowsResourceOperationFailureWhenNoCustomerFound() {
		when(customerRepository.getCustomer(USER_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(CUSTOMER_WAS_NOT_FOUND)));
		emailEntityRepository.findOne(getEmailIdentifier())
				.test()
				.assertNotComplete()
				.assertError(ResourceOperationFailure.class);
	}

	@Test
	public void findAllProducesCorrectEmailIdentifier() {
		when(customerRepository.getCustomer(USER_ID)).thenReturn(Single.just(customer));
		emailEntityRepository.findAll(StringIdentifier.of(SCOPE))
				.test()
				.assertNoErrors()
				.assertValue(getEmailIdentifier());
	}

	@Test
	public void findAllDoesNotCompleteAndFailsWhenCustomerNotFound() {
		when(customerRepository.getCustomer(USER_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(CUSTOMER_WAS_NOT_FOUND)));
		emailEntityRepository.findAll(StringIdentifier.of(SCOPE))
				.test()
				.assertNotComplete()
				.assertError(ResourceOperationFailure.class);
	}

	@Test
	public void creatingValidEmailSucceedsAndReturnsCorrectEmailIdentifier() {
		when(customerRepository.getCustomer(USER_ID)).thenReturn(Single.just(customer));
		when(customerRepository.updateCustomerAsCompletable(customer)).thenReturn(Completable.complete());
		emailEntityRepository.submit(getEmailEntity(), StringIdentifier.of(SCOPE))
				.test()
				.assertNoErrors()
				.assertValue(getSubmitResult());
	}

	@Test
	public void createDoesNotCompleteAndFailsWhenCustomerNotFound() {
		when(customerRepository.getCustomer(USER_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(CUSTOMER_WAS_NOT_FOUND)));
		emailEntityRepository.submit(getEmailEntity(), StringIdentifier.of(SCOPE))
				.test()
				.assertNotComplete()
				.assertError(ResourceOperationFailure.class);
	}

	private SubmitResult<EmailIdentifier> getSubmitResult() {
		return SubmitResult.<EmailIdentifier>builder()
				.withIdentifier(getEmailIdentifier())
				.withStatus(SubmitStatus.CREATED)
				.build();
	}

	private EmailIdentifier getEmailIdentifier() {

		return EmailIdentifier.builder()
				.withEmailId(StringIdentifier.of(EXPECTED_EMAIL))
				.withEmails(EmailsIdentifier.builder().withScope(StringIdentifier.of(SCOPE)).build())
				.build();
	}

	private EmailEntity getEmailEntity() {
		return EmailEntity.builder()
				.withEmailId(USER_ID)
				.withEmail(EXPECTED_EMAIL)
				.build();
	}


}