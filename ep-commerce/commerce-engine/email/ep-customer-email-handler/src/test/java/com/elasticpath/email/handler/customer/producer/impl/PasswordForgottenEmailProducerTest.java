/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.customer.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
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
 * Unit test for {@link PasswordForgottenEmailProducer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PasswordForgottenEmailProducerTest {

	private static final String CUSTOMER_GUID = "customerGuid1";

	private static final String NEW_PASSWORD_VALUE = "newPassword";

	private static final String NEW_PASSWORD_KEY = "newPassword";

	@Mock
	private CustomerEmailPropertyHelper customerEmailPropertyHelper;

	@Mock
	private EmailComposer emailComposer;

	@Mock
	private CustomerService customerService;

	@InjectMocks
	private PasswordForgottenEmailProducer emailProducer;

	@Test
	public void verifyPasswordForgottenEmailIsCreatedFromCustomerGuid() throws Exception {
		final EmailDto expectedEmail = EmailDto.builder().build();

		final EmailProperties emailProperties = mock(EmailProperties.class);
		final Customer customer = mock(Customer.class);

		when(customerService.findByGuid(CUSTOMER_GUID))
				.thenReturn(customer);

		when(customerEmailPropertyHelper.getForgottenPasswordEmailProperties(customer, NEW_PASSWORD_VALUE))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		final EmailDto actualEmail = emailProducer.createEmail(CUSTOMER_GUID, Collections.singletonMap(NEW_PASSWORD_KEY, NEW_PASSWORD_VALUE));

		assertThat(actualEmail)
				.as("Unexpected email created by producer")
				.isSameAs(expectedEmail);
	}

	@Test
	public void verifyExceptionThrownWhenNoNewPasswordProvided() throws Exception {
		assertThatThrownBy(() -> emailProducer.createEmail(CUSTOMER_GUID, new HashMap<>()))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyExceptionThrownWhenNoCustomerIsFound() throws Exception {
		when(customerService.findByGuid(CUSTOMER_GUID)).thenReturn(null);

		final Map<String, Object> data = Collections.singletonMap(NEW_PASSWORD_KEY, NEW_PASSWORD_VALUE);

		assertThatThrownBy(() -> emailProducer.createEmail(CUSTOMER_GUID, data))
				.isInstanceOf(IllegalArgumentException.class);
	}

}
