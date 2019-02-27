/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email.handler.producer.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import com.elasticpath.email.handler.producer.LegacyEmailProducer;

/**
 * Convenience abstract class to handle cases in which only one email needs to be sent.
 *
 * @deprecated use {@link com.elasticpath.email.producer.spi.AbstractEmailProducer}.
 */
@Deprecated
public abstract class AbstractLegacyEmailProducer implements LegacyEmailProducer {

	@Override
	public Collection<Email> createEmails(final String guid, final Map<String, Object> emailData) throws EmailException {
		return Collections.singleton(createEmail(guid, emailData));
	}

	/**
	 * Creates a populated {@link Email} message.
	 *
	 * @param guid the GUID corresponding to the domain object for which an email must be produced
	 * @param emailData additional data needed to construct the email
	 * @return a populated {@link Email} instance
	 * @throws EmailException in case of error constructing email
	 */
	public abstract Email createEmail(String guid, Map<String, Object> emailData) throws EmailException;

}
