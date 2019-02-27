/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email.handler.order.producer.impl;

import java.util.Map;

import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.EmailNotificationHelper;
import com.elasticpath.email.producer.spi.AbstractEmailProducer;
import com.elasticpath.email.producer.spi.composer.EmailComposer;

/**
 * Creates an Order Confirmation {@link EmailDto} for a given order number.
 */
public class OrderConfirmationEmailProducer extends AbstractEmailProducer {

	private EmailComposer emailComposer;

	private EmailNotificationHelper emailNotificationHelper;

	@Override
	public EmailDto createEmail(final String guid, final Map<String, Object> emailData) {
		final EmailProperties orderEmailProperties = getEmailNotificationHelper().getOrderEmailProperties(guid);
		return getEmailComposer().composeMessage(orderEmailProperties);
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
