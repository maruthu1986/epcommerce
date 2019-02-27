/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.cmuser.producer.impl;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;

/**
 * Producer for a CM User account creation email.
 */
public class CmUserCreatedEmailProducer extends AbstractCmUserEmailProducer {

	@Override
	public EmailDto createEmail(final String guid, final Map<String, Object> emailData) {

		final CmUser cmUser = getCmUser(guid);
		final String password = getPassword(emailData);
		final Locale locale = getLocale(emailData);

		final EmailProperties properties = getCmUserEmailPropertyHelper().getCreateEmailProperties(cmUser, password, locale);

		return getEmailComposer().composeMessage(properties);
	}

}
