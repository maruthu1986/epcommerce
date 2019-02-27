/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.producer.impl;

import java.util.Map;

import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.OrderEmailPropertyHelper;
import com.elasticpath.email.producer.spi.AbstractEmailProducer;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.order.OrderService;

/**
 * Creates an Order Shipment Release Failed {@link EmailDto} for a given order shipment number.
 */
public class OrderShipmentReleaseFailedEmailProducer extends AbstractEmailProducer {

	private EmailComposer emailComposer;

	private OrderEmailPropertyHelper orderEmailPropertyHelper;

	private OrderService orderService;

	private static final String SHIPMENT_TYPE_KEY = "shipmentType";

	private static final String ERROR_MESSAGE_KEY = "errorMessage";

	private static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE = "The emailData must contain a non-null '%s' value.";

	private static final String ORDER_SHIPMENT_NOT_FOUND_MESSAGE_TEMPLATE = "No %s OrderShipment was found for Shipment Number [%s].";

	@Override
	public EmailDto createEmail(final String shipmentNumber, final Map<String, Object> emailData) {
		final String errorMessage = getErrorMessage(emailData);
		final OrderShipment shipment = getOrderShipment(shipmentNumber, emailData);

		final EmailProperties emailProperties = getOrderEmailPropertyHelper().getFailedShipmentPaymentEmailProperties(shipment, errorMessage);

		return getEmailComposer().composeMessage(emailProperties);
	}

	/**
	 * Retrieves a {@link OrderShipment}.
	 * 
	 * @param shipmentNumber the shipment number ID
	 * @param emailData the contextual email data
	 * @return a {@link OrderShipment}
	 * @throws IllegalArgumentException if an {@link OrderShipment} cannot be retrieved from the given parameters
	 */
	protected OrderShipment getOrderShipment(final String shipmentNumber, final Map<String, Object> emailData) {
		final ShipmentType shipmentType = getShipmentType(emailData);

		final OrderShipment orderShipment = getOrderService().findOrderShipment(shipmentNumber, shipmentType);

		if (orderShipment == null) {
			throw new IllegalArgumentException(String.format(ORDER_SHIPMENT_NOT_FOUND_MESSAGE_TEMPLATE, shipmentType, shipmentNumber));
		}

		return orderShipment;
	}

	/**
	 * Retrieves the error message.
	 * 
	 * @param emailData the contextual email data
	 * @return the error message
	 * @throws IllegalArgumentException if an error message cannot be retrieved from the given parameters
	 */
	protected String getErrorMessage(final Map<String, Object> emailData) {
		return getObjectFromEmailData(ERROR_MESSAGE_KEY, emailData);
	}

	private ShipmentType getShipmentType(final Map<String, Object> emailData) {
		return ShipmentType.valueOf(getObjectFromEmailData(SHIPMENT_TYPE_KEY, emailData));
	}

	/**
	 * Retrieves a String value from the given {@code Map} of email contextual data.
	 * 
	 * @param key the object key
	 * @param emailData email contextual data
	 * @return the String
	 * @throws IllegalArgumentException if the Object can not be retrieved from the given parameters
	 */
	protected String getObjectFromEmailData(final String key, final Map<String, Object> emailData) {
		if (emailData == null || !emailData.containsKey(key) || emailData.get(key) == null) {
			throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE, key));
		}

		return String.valueOf(emailData.get(key));
	}

	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	protected EmailComposer getEmailComposer() {
		return emailComposer;
	}

	public void setOrderEmailPropertyHelper(final OrderEmailPropertyHelper orderEmailPropertyHelper) {
		this.orderEmailPropertyHelper = orderEmailPropertyHelper;
	}

	protected OrderEmailPropertyHelper getOrderEmailPropertyHelper() {
		return orderEmailPropertyHelper;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	protected OrderService getOrderService() {
		return orderService;
	}
}
