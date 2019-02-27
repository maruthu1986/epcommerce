/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email.util;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import com.elasticpath.email.domain.EmailProperties;

/**
 * Class for composing an email message.
 *
 * @deprecated use {@link com.elasticpath.email.producer.spi.composer.EmailComposer}.
 */
@Deprecated
public interface LegacyEmailComposer {

	/**
	 * Composes an email using the provided email properties.
	 *
	 * @param emailProperties the properties used to create the email
	 * @return an Email message.
	 * @throws EmailException an email exception
	 */
	Email composeMessage(EmailProperties emailProperties) throws EmailException;

}
