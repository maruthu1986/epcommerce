/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email.handler.order.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.email.EmailDto.builder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.EmailNotificationHelper;
import com.elasticpath.email.producer.spi.composer.EmailComposer;

/**
 * Test class for {@link OrderConfirmationEmailProducer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationEmailProducerTest {

	@Mock
	private EmailNotificationHelper emailNotificationHelper;

	@Mock
	private EmailComposer emailComposer;

	@InjectMocks
	private OrderConfirmationEmailProducer producer;

	@Test
	public void verifyOrderConfirmationEmailIsCreatedFromOrderNumber() throws Exception {
		final EmailDto expectedEmail = builder().build();
		final String orderNumber = "order123";

		final EmailProperties emailProperties = mock(EmailProperties.class);

		when(emailNotificationHelper.getOrderEmailProperties(orderNumber))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		final EmailDto actualEmail = producer.createEmail(orderNumber, null);

		assertThat(actualEmail)
				.as("Unexpected email created by producer")
				.isSameAs(expectedEmail);
	}

}
