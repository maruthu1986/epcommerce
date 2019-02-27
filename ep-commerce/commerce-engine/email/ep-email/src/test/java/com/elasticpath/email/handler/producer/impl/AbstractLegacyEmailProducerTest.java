/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email.handler.producer.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Test;

/**
 * Test class for {@link AbstractLegacyEmailProducer}.
 */
public class AbstractLegacyEmailProducerTest {

	private AbstractLegacyEmailProducer abstractEmailProducer;

	@Test
	public void verifyCreateEmailsIsSimpleWrapperForCreateEmail() throws Exception {
		final Email email = new SimpleEmail();

		abstractEmailProducer = new AbstractLegacyEmailProducer() {
			@Override
			public Email createEmail(final String guid, final Map<String, Object> emailData) throws EmailException {
				return email;
			}
		};

		final Collection<Email> emails = abstractEmailProducer.createEmails(null, null);
		assertEquals("Returned Collection of emails should have exactly one entry", 1, emails.size());
		assertEquals("Expected Collection of emails to contain only the single Email", email, emails.iterator().next());
	}

}