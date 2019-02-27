/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.elasticpath.email.EmailDto;
import com.elasticpath.email.producer.api.EmailProducer;

/**
 * Base SPI class for {@link EmailProducer} implementations.
 */
public abstract class AbstractEmailProducer implements EmailProducer {

	/**
	 * Creates a populated {@link EmailDto} message. Convenience method for producers that send a single email.
	 *
	 * @param guid      the GUID corresponding to the domain object for which an email must be produced
	 * @param emailData additional data needed to construct the email
	 * @return a populated {@link EmailDto} instance
	 */
	public abstract EmailDto createEmail(String guid, Map<String, Object> emailData);

	@Override
	public Collection<EmailDto> createEmails(final String guid, final Map<String, Object> emailData) {
		return Collections.singleton(createEmail(guid, emailData));
	}

}
