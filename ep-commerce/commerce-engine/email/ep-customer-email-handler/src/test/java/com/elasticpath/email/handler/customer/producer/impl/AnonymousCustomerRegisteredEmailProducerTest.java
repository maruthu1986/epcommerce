/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.customer.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.email.EmailDto.builder;

import java.util.HashMap;
import java.util.Map;

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
 * Test class for {@link AnonymousCustomerRegisteredEmailProducerTest}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AnonymousCustomerRegisteredEmailProducerTest {

	private static final String CUSTOMER_GUID = "customerGuid1";
	private static final String PASSWORD_KEY = "password";
	private static final String PASSWORD = "newPassword";

	@Mock
	private CustomerEmailPropertyHelper customerEmailPropertyHelper;

	@Mock
	private EmailComposer emailComposer;

	@Mock
	private CustomerService customerService;

	@InjectMocks
	private AnonymousCustomerRegisteredEmailProducer emailProducer;

	@Test
	public void verifyNewlyRegisteredCustomerEmailIsCreatedFromCustomerGuid() throws Exception {
		final EmailDto expectedEmail = builder().build();

		final EmailProperties emailProperties = mock(EmailProperties.class);
		final Customer customer = mock(Customer.class);

		when(customerService.findByGuid(CUSTOMER_GUID))
				.thenReturn(customer);

		when(customerEmailPropertyHelper.getNewlyRegisteredCustomerEmailProperties(customer, PASSWORD))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		final EmailDto actualEmail = emailProducer.createEmail(CUSTOMER_GUID, buildValidAdditionalData(PASSWORD));

		assertThat(actualEmail)
				.as("Unexpected email created by producer")
				.isSameAs(expectedEmail);
	}

	@Test
	public void verifyExceptionThrownWhenNoCustomerIsFound() throws Exception {
		when(customerService.findByGuid(CUSTOMER_GUID)).thenReturn(null);

		assertThatThrownBy(() -> emailProducer.createEmail(CUSTOMER_GUID, buildValidAdditionalData(PASSWORD)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyExceptionThrownWhenNoPasswordIsSupplied() throws Exception {
		when(customerService.findByGuid(CUSTOMER_GUID)).thenReturn(null);

		final Map<String, Object> additionalData = buildValidAdditionalData(PASSWORD);
		additionalData.remove(PASSWORD_KEY);

		assertThatThrownBy(() -> emailProducer.createEmail(CUSTOMER_GUID, additionalData))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private Map<String, Object> buildValidAdditionalData(final String password) {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put(PASSWORD_KEY, password);
		return additionalData;
	}

}
