/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.OrderEmailPropertyHelper;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.order.OrderService;

/**
 * Test class for {@link OrderShipmentReleaseFailedEmailProducer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderShipmentReleaseFailedEmailProducerTest {

	private static final String ORDER_GUID = "ORDER-001";
	private static final String ORDER_GUID_KEY = "orderGuid";
	private static final String SHIPMENT_TYPE_KEY = "shipmentType";
	private static final String ERROR_MESSAGE_KEY = "errorMessage";
	private static final String SHIPMENT_TYPE_NAME = "PHYSICAL";
	private static final String ERROR_MESSAGE = "Oh no!";

	@Mock
	private OrderEmailPropertyHelper orderEmailPropertyHelper;

	@Mock
	private EmailComposer emailComposer;

	@Mock
	private OrderService orderService;

	@InjectMocks
	private OrderShipmentReleaseFailedEmailProducer producer;

	@Test
	public void verifyEmailProducerFromEventMessage() {
		final EmailDto expectedEmail = EmailDto.builder().build();
		final String shipmentNumber = "SHIP-001";

		final OrderShipment shipment = mock(OrderShipment.class);

		final Map<String, Object> data = ImmutableMap.of(
				ERROR_MESSAGE_KEY, ERROR_MESSAGE,
				ORDER_GUID_KEY, ORDER_GUID, // this is not used by the producer, but real messages will contain this field anyway.
				SHIPMENT_TYPE_KEY, SHIPMENT_TYPE_NAME);

		when(orderService.findOrderShipment(shipmentNumber, ShipmentType.PHYSICAL))
				.thenReturn(shipment);

		final EmailProperties emailProperties = mock(EmailProperties.class);

		when(orderEmailPropertyHelper.getFailedShipmentPaymentEmailProperties(shipment, ERROR_MESSAGE))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		assertThat(producer.createEmail(shipmentNumber, data))
				.as("Unexpected email created")
				.isSameAs(expectedEmail);
	}

	@Test
	public void verifyExceptionThrownWhenNoErrorMessageGiven() {
		final Map<String, Object> data = new HashMap<>();
		data.put(ERROR_MESSAGE_KEY, null);
		data.put(SHIPMENT_TYPE_KEY, SHIPMENT_TYPE_NAME);

		assertThatThrownBy(() -> producer.createEmail(ORDER_GUID, data))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(ERROR_MESSAGE_KEY);
	}

	@Test
	public void verifyExceptionThrownWhenNoShipmentTypeGiven() {
		final Map<String, Object> data = new HashMap<>();
		data.put(ERROR_MESSAGE_KEY, ERROR_MESSAGE);
		data.put(SHIPMENT_TYPE_KEY, null);

		assertThatThrownBy(() -> producer.createEmail(ORDER_GUID, data))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(SHIPMENT_TYPE_KEY);
	}

}