/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.email.EmailDto.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
 * Unit test for {@link OrderShipmentShippedEmailProducer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderShipmentShippedEmailProducerTest {

	private static final String ORDER_GUID_KEY = "orderGuid";

	private static final String ORDER_GUID = "order123";

	private static final String SHIPMENT_NUMBER = "shipment1";

	@Mock
	private EmailNotificationHelper emailNotificationHelper;

	@Mock
	private EmailComposer emailComposer;

	@InjectMocks
	private OrderShipmentShippedEmailProducer producer;

	@Test
	public void verifyOrderShippedEmailIsCreatedFromOrderAndShipmentNumbers() throws Exception {
		final EmailDto expectedEmail = builder().build();

		final EmailProperties emailProperties = mock(EmailProperties.class);

		when(emailNotificationHelper.getShipmentConfirmationEmailProperties(ORDER_GUID, SHIPMENT_NUMBER))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		final Map<String, Object> data = new HashMap<>();
		data.put(ORDER_GUID_KEY, ORDER_GUID);

		final EmailDto actualEmail = producer.createEmail(SHIPMENT_NUMBER, data);

		assertThat(actualEmail)
				.as("Unexpected email created by producer")
				.isSameAs(expectedEmail);
	}

	@Test
	public void verifyExceptionThrownWhenNoOrderGuidGiven() throws Exception {
		assertThatThrownBy(() -> producer.createEmail(ORDER_GUID, Collections.singletonMap(ORDER_GUID_KEY, null)))
				.isInstanceOf(IllegalArgumentException.class);
	}

}
