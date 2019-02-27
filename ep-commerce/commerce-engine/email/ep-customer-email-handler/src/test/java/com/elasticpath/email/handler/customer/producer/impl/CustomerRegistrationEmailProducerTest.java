/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.customer.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.email.EmailDto.builder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.customer.helper.CustomerEmailPropertyHelper;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.customer.CustomerService;

/**
 * Test class for {@link CustomerRegistrationEmailProducer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerRegistrationEmailProducerTest {

	private static final String CUSTOMER_GUID = "customerGuid1";

	@Mock
	private CustomerEmailPropertyHelper customerEmailPropertyHelper;

	@Mock
	private EmailComposer emailComposer;

	@Mock
	private CustomerService customerService;

	@InjectMocks
	private CustomerRegistrationEmailProducer emailProducer;

	@Test
	public void verifyCustomerRegistrationEmailIsCreatedFromCustomerGuid() throws Exception {
		final EmailDto expectedEmail = builder().build();

		final EmailProperties emailProperties = mock(EmailProperties.class);
		final Customer customer = mock(Customer.class);

		when(customerService.findByGuid(CUSTOMER_GUID)).thenReturn(customer);

		when(customerEmailPropertyHelper.getNewAccountEmailProperties(customer))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		final EmailDto actualEmail = emailProducer.createEmail(CUSTOMER_GUID, null);

		assertThat(actualEmail)
				.as("Unexpected email created by producer")
				.isSameAs(expectedEmail);
	}

	@Test
	public void verifyExceptionThrownWhenNoCustomerIsFound() throws Exception {
		when(customerService.findByGuid(CUSTOMER_GUID)).thenReturn(null);

		assertThatThrownBy(() -> emailProducer.createEmail(CUSTOMER_GUID, null))
				.isInstanceOf(IllegalArgumentException.class);
	}

}
