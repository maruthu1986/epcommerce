/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.dataimport.producer.impl;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.LocaleUtils;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.dataimport.helper.ImportEmailPropertyHelper;
import com.elasticpath.email.producer.spi.AbstractEmailProducer;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;

/**
 * ImportJobCompletedEmailProducer.
 */
public class ImportJobCompletedEmailProducer extends AbstractEmailProducer {

	private EmailComposer emailComposer;

	private ImportEmailPropertyHelper importEmailPropertyHelper;

	private ImportJobStatusHandler importJobStatusHandler;

	private CmUserService cmUserService;

	private static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE = "The emailData must contain a non-null '%s' value.";

	private static final String LOCALE_KEY = "locale";

	private static final String CM_USER_KEY = "cmUserGuid";

	@Override
	public EmailDto createEmail(final String guid, final Map<String, Object> emailData) {
		final CmUser cmUser = getCmUser(emailData);

		if (cmUser == null) {
			return null;
		}

		final Locale locale = getLocale(emailData);
		final ImportJobStatus importJobStatus = getImportJobStatus(guid);
		final EmailProperties emailProperties = getImportEmailPropertyHelper().getEmailProperties(importJobStatus, cmUser, locale);

		return getEmailComposer().composeMessage(emailProperties);
	}

	/**
	 * Retrieves the {@link ImportJobStatus} from the given {@code Map} of email contextual data.
	 *
	 * @param importJobProcessId the Import Job's process ID
	 * @return the {@link ImportJobStatus}
	 */
	protected ImportJobStatus getImportJobStatus(final String importJobProcessId) {
		if (importJobProcessId == null) {
			throw new IllegalArgumentException("The Import Job Process ID GUID must not be null");
		}

		final ImportJobStatus status = getImportJobStatusHandler().getImportJobStatus(importJobProcessId);

		if (status == null) {
			throw new IllegalArgumentException("Could not locate an Import Job Status with processId [" + importJobProcessId + "]");
		}

		return status;
	}

	/**
	 * Retrieves a {@link CmUser} from the given {@code Map} of email contextual data.
	 *
	 * @param emailData email contextual data
	 * @return a {@link CmUser}, or null if no cmUser guid has been supplied.
	 * @throws IllegalArgumentException if the emailData is null
	 */
	protected CmUser getCmUser(final Map<String, Object> emailData) {
		if (emailData == null) {
			throw new IllegalArgumentException("The emailData must not be null.");
		}

		final Object cmUserGuidObject = emailData.get(CM_USER_KEY);
		if (cmUserGuidObject == null) {
			return null;
		}

		final String guid = String.valueOf(cmUserGuidObject);
		final CmUser cmUser = getCmUserService().findByGuid(guid);

		if (cmUser == null) {
			throw new IllegalArgumentException("Could not locate a CM User with GUID [" + guid + "]");
		}

		return cmUser;
	}

	/**
	 * Retrieves the {@link Locale} from the given {@code Map} of email contextual data.
	 *
	 * @param emailData email contextual data
	 * @return the {@link Locale}
	 * @throws IllegalArgumentException if the Locale can not be retrieved from the given parameters
	 */
	protected Locale getLocale(final Map<String, Object> emailData) {
		return LocaleUtils.toLocale(getStringFromEmailData(LOCALE_KEY, emailData));
	}

	/**
	 * Retrieves a String from the given {@code Map} of email contextual data.
	 *
	 * @param key the key
	 * @param emailData email contextual data
	 * @return the Object
	 * @throws IllegalArgumentException if the Object can not be retrieved from the given parameters
	 */
	protected String getStringFromEmailData(final String key, final Map<String, Object> emailData) {
		if (emailData == null || !emailData.containsKey(key) || emailData.get(key) == null) {
			throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE, key));
		}

		return String.valueOf(emailData.get(key));
	}

	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	protected EmailComposer getEmailComposer() {
		return emailComposer;
	}

	protected ImportEmailPropertyHelper getImportEmailPropertyHelper() {
		return importEmailPropertyHelper;
	}

	public void setImportEmailPropertyHelper(final ImportEmailPropertyHelper importEmailPropertyHelper) {
		this.importEmailPropertyHelper = importEmailPropertyHelper;
	}

	protected ImportJobStatusHandler getImportJobStatusHandler() {
		return importJobStatusHandler;
	}

	public void setImportJobStatusHandler(final ImportJobStatusHandler importJobStatusHandler) {
		this.importJobStatusHandler = importJobStatusHandler;
	}

	protected CmUserService getCmUserService() {
		return cmUserService;
	}

	public void setCmUserService(final CmUserService cmUserService) {
		this.cmUserService = cmUserService;
	}

}
