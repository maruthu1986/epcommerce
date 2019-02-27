/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.producer.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.ReturnExchangeEmailPropertyHelper;
import com.elasticpath.email.producer.spi.AbstractEmailProducer;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.order.ReturnAndExchangeService;

/**
 * Creates an RMA Receipt {@link EmailDto} when an {@link OrderReturn} has been created.
 */
public class ReturnExchangeEmailProducer extends AbstractEmailProducer {

	private ReturnAndExchangeService returnAndExchangeService;

	private ReturnExchangeEmailPropertyHelper returnExchangeEmailPropertyHelper;

	private EmailComposer emailComposer;

	private static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE = "The emailData must contain a non-null '%s' value.";

	private static final String UID_KEY = "UID";

	private static final String EMAIL_KEY = "EMAIL";

	@Override
	public EmailDto createEmail(final String guid, final Map<String, Object> emailData) {
		final OrderReturn orderReturn = getOrderReturn(emailData);

		final EmailProperties emailProperties = getReturnExchangeEmailPropertyHelper().getOrderReturnEmailProperties(orderReturn);

		EmailDto email = getEmailComposer().composeMessage(emailProperties);

		final Optional<String> recipient = getEmailAddress(orderReturn, emailData);

		if (recipient.isPresent()) {
			email = EmailDto.builder()
					.fromPrototype(email)
					.withTo(recipient.get())
					.build();
		}

		return email;
	}

	/**
	 * Retrieves an {@link OrderReturn} with the given uid.
	 * 
	 * @param emailData email contextual data
	 * @return a {@link OrderReturn}
	 * @throws IllegalArgumentException if an {@link OrderReturn} can not be retrieved from the given parameters
	 */
	protected OrderReturn getOrderReturn(final Map<String, Object> emailData) {
		final Long uid = Long.valueOf((Integer) getObjectFromEmailData(UID_KEY, emailData));
		final Collection<Long> uids = Collections.singletonList(uid);

		final List<OrderReturn> orderReturns = getReturnAndExchangeService().findByUids(uids);

		if (orderReturns == null || orderReturns.isEmpty()) {
			throw new IllegalArgumentException("Could not locate a OrderReturn with uid [" + uid + "]");
		}

		return orderReturns.get(0);
	}

	/**
	 * Checks the contextual data for an optional overriding email address.
	 * 
	 * @param orderReturn the order return
	 * @param emailData email contextual data
	 * @return the recipient email address
	 */
	protected Optional<String> getEmailAddress(final OrderReturn orderReturn, final Map<String, Object> emailData) {
		final Object emailValue = emailData.get(EMAIL_KEY);

		return Optional.ofNullable(emailValue)
				.map(String::valueOf)
				.filter(StringUtils::isNotBlank);
	}

	/**
	 * Retrieves an Object from the given {@code Map} of email contextual data.
	 * 
	 * @param key the object key
	 * @param emailData email contextual data
	 * @return the Object
	 * @throws IllegalArgumentException if the Object can not be retrieved from the given parameters
	 */
	protected Object getObjectFromEmailData(final String key, final Map<String, Object> emailData) {
		if (emailData == null || !emailData.containsKey(key) || emailData.get(key) == null) {
			throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE, key));
		}

		return emailData.get(key);
	}

	public void setReturnExchangeEmailPropertyHelper(final ReturnExchangeEmailPropertyHelper orderEmailPropertyHelper) {
		this.returnExchangeEmailPropertyHelper = orderEmailPropertyHelper;
	}

	protected ReturnExchangeEmailPropertyHelper getReturnExchangeEmailPropertyHelper() {
		return returnExchangeEmailPropertyHelper;
	}

	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	protected EmailComposer getEmailComposer() {
		return emailComposer;
	}

	public void setReturnAndExchangeService(final ReturnAndExchangeService returnAndExchangeService) {
		this.returnAndExchangeService = returnAndExchangeService;
	}

	public ReturnAndExchangeService getReturnAndExchangeService() {
		return this.returnAndExchangeService;
	}

}
