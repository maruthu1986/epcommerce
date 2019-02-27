/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.cmuser.producer.impl;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.email.handler.cmuser.helper.CmUserEmailPropertyHelper;
import com.elasticpath.email.producer.spi.AbstractEmailProducer;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Common abstract parent class for CM User Email Producers.
 */
public abstract class AbstractCmUserEmailProducer extends AbstractEmailProducer {

	private static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE = "The emailData must contain a non-null '%s' value.";

	private static final String LOCALE_KEY = "locale";

	private static final String PASSWORD_KEY = "password";

	private EmailComposer emailComposer;

	private CmUserEmailPropertyHelper cmUserEmailPropertyHelper;

	private CmUserService cmUserService;

	/**
	 * Retrieves a {@link CmUser} with the given guid.
	 * 
	 * @param guid the CM User GUID
	 * @return a {@link CmUser}
	 * @throws IllegalArgumentException if an {@link CmUser} can not be retrieved from the given parameters
	 */
	protected CmUser getCmUser(final String guid) {
		if (guid == null) {
			throw new IllegalArgumentException("A CM User guid must be provided.");
		}

		final CmUser cmUser = getCmUserService().findByGuid(guid);

		if (cmUser == null) {
			throw new IllegalArgumentException("Could not locate a CM User with GUID [" + guid + "]");
		}

		return cmUser;
	}

	/**
	 * Retrieves the Password from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return the password
	 * @throws IllegalArgumentException if the Locale can not be retrieved from the given parameters
	 */
	protected String getPassword(final Map<String, Object> emailData) {
		return String.valueOf(getObjectFromEmailData(PASSWORD_KEY, emailData));
	}

	/**
	 * Retrieves the {@link Locale} from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return the {@link Locale}
	 * @throws IllegalArgumentException if the Locale can not be retrieved from the given parameters
	 */
	protected Locale getLocale(final Map<String, Object> emailData) {
		final String languageTag = String.valueOf(getObjectFromEmailData(LOCALE_KEY, emailData));
		return Locale.forLanguageTag(languageTag);
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

	public void setCmUserEmailPropertyHelper(final CmUserEmailPropertyHelper cmUserEmailPropertyHelper) {
		this.cmUserEmailPropertyHelper = cmUserEmailPropertyHelper;
	}

	protected CmUserEmailPropertyHelper getCmUserEmailPropertyHelper() {
		return cmUserEmailPropertyHelper;
	}

	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	protected EmailComposer getEmailComposer() {
		return emailComposer;
	}

	protected CmUserService getCmUserService() {
		return cmUserService;
	}

	public void setCmUserService(final CmUserService cmUserService) {
		this.cmUserService = cmUserService;
	}

}
