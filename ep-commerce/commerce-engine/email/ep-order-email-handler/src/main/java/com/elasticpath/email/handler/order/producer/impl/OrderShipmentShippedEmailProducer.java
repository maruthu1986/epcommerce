/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.producer.impl;

import java.util.Map;

import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.EmailNotificationHelper;
import com.elasticpath.email.producer.spi.AbstractEmailProducer;
import com.elasticpath.email.producer.spi.composer.EmailComposer;

/**
 * Produces an order shipped {@link EmailDto} for the given shipment.
 */
public class OrderShipmentShippedEmailProducer extends AbstractEmailProducer {

	private EmailComposer emailComposer;

	private EmailNotificationHelper emailNotificationHelper;

	private static final String ORDER_GUID_KEY = "orderGuid";

	@Override
	public EmailDto createEmail(final String orderShipmentNumber, final Map<String, Object> emailData) {
		final String orderGuid = getOrderGuid(emailData);

		final EmailProperties orderEmailProperties = getEmailNotificationHelper()
				.getShipmentConfirmationEmailProperties(orderGuid, orderShipmentNumber);

		return getEmailComposer().composeMessage(orderEmailProperties);
	}

	private String getOrderGuid(final Map<String, Object> emailData) {
		if (emailData == null || !emailData.containsKey(ORDER_GUID_KEY) || emailData.get(ORDER_GUID_KEY) == null) {
			throw new IllegalArgumentException("The emailData must contain a non-null '" + ORDER_GUID_KEY + "' value.");
		}

		return String.valueOf(emailData.get(ORDER_GUID_KEY));
	}

	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	protected EmailComposer getEmailComposer() {
		return emailComposer;
	}

	public void setEmailNotificationHelper(final EmailNotificationHelper emailNotificationHelper) {
		this.emailNotificationHelper = emailNotificationHelper;
	}

	protected EmailNotificationHelper getEmailNotificationHelper() {
		return emailNotificationHelper;
	}

}
