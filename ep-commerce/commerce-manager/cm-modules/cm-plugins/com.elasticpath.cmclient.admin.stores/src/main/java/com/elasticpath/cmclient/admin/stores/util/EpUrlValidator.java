/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.UrlValidator;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;

/**
 * Represents the url validator.
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class EpUrlValidator implements IValidator {
		
	private static final String START_WITH_VALID_SCHEMA = "^http[s]?://"; //$NON-NLS-1$
	
	private static final String LOCALHOST = "localhost"; //$NON-NLS-1$
	
	private static final String LOCALHOST_IP = "127.0.0.1"; //$NON-NLS-1$

	@Override
	public IStatus validate(final Object value) {
		final String stringValue = (String) value;
		final Matcher matcher = Pattern.compile(START_WITH_VALID_SCHEMA).matcher(stringValue);
		if (matcher.find()) {
			final String convertedValue = convertUrl(stringValue, matcher.end());

			final UrlValidator urlValidator = new UrlValidator();
			if (!urlValidator.isValid(convertedValue)) {
				return createErrorStatus(AdminStoresMessages.get().EpValidatorUrlIncorrect);
			}
		} else if (stringValue.length() != 0) {
			return createErrorStatus(AdminStoresMessages.get().EpValidatorHttpOrHttpsIsRequired);
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
		return new Status(IStatus.ERROR, AdminStoresPlugin.PLUGIN_ID, IStatus.ERROR, message, null);
	}
}
