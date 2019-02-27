/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email.handler.producer;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

/**
 * Creates an {@link Email} message.
 *
 * @deprecated use {@link com.elasticpath.email.producer.api.EmailProducer}.
 */
@Deprecated
public interface LegacyEmailProducer {

	/**
	 * Creates populated {@link Email} messages.
	 *
	 * @param guid      the GUID corresponding to the domain object for which an email must be produced
	 * @param emailData additional data needed to construct the email
	 * @return a Collection of populated {@link Email} instances
	 * @throws EmailException in case of error constructing email
	 */
	Collection<Email> createEmails(String guid, Map<String, Object> emailData) throws EmailException;

}
