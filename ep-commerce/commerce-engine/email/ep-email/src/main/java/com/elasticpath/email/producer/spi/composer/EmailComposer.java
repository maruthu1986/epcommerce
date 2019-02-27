/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer;

import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;

/**
 * Class for composing an email message.
 */
public interface EmailComposer {

	/**
	 * Composes an email using the provided email properties.
	 *
	 * @param emailProperties the properties used to create the email
	 * @return an email DTO
	 */
	EmailDto composeMessage(EmailProperties emailProperties);

}
