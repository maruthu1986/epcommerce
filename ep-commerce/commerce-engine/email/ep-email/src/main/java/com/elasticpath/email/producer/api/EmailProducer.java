/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.api;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;

import com.elasticpath.email.EmailDto;

/**
 * Creates an {@link EmailDto} message.
 */
public interface EmailProducer extends BiFunction<String, Map<String, Object>, Collection<EmailDto>> {

	/**
	 * Creates populated {@link EmailDto} messages.
	 *
	 * @param guid      the GUID corresponding to the domain object for which an email must be produced
	 * @param emailData additional data needed to construct the email
	 * @return a Collection of populated {@link EmailDto} instances
	 */
	Collection<EmailDto> createEmails(String guid, Map<String, Object> emailData);

	@Override
	default Collection<EmailDto> apply(String guid, Map<String, Object> emailData) {
		return createEmails(guid, emailData);
	}

}
