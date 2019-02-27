/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.core.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.UrlValidator;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;

/**
 * Represents the url validator.
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class EpUrlValidator implements IValidator {

	private static final String DEFAULT_VALIDATION_PATTERN = "^(http|ftp)[s]?://"; //$NON-NLS-1$

	private static final String LOCALHOST = "localhost"; //$NON-NLS-1$

	private static final String LOCALHOST_IP = "127.0.0.1"; //$NON-NLS-1$



	@Override
	public IStatus validate(final Object value) {
		final String stringValue = (String) value;
		final Matcher matcher = Pattern.compile(DEFAULT_VALIDATION_PATTERN).matcher(stringValue);
		if (matcher.find()) {
			final String convertedValue = convertUrl(stringValue, matcher.end());

			final UrlValidator urlValidator = new UrlValidator();
			if (!urlValidator.isValid(convertedValue)) {
				return createErrorStatus(CoreMessages.get().ValidationError_UrlIncorrect);
			}
		} else if (stringValue.length() != 0) {
			return createErrorStatus(CoreMessages.get().ValidationError_HttpOrHttpsIsRequired);
		}
		return Status.OK_STATUS;
	}

	private String convertUrl(final String stringValue, final int startHostIndex) {
		String value = stringValue;
		if (value.startsWith(LOCALHOST, startHostIndex)) {
			value = value.replaceFirst(LOCALHOST, LOCALHOST_IP);
		}
		return value;
	}

	private Status createErrorStatus(final String message) {
		return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, message, null);
	}

}
