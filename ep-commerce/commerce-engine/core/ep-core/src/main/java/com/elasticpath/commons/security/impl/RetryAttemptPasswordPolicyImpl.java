/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security.impl;

import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Implementation of <code>PasswordPolicy</code> that validates a password's maximum number of retry attempts for a <code>PasswordHolder</code>.
 */
public class RetryAttemptPasswordPolicyImpl extends AbstractPasswordPolicyImpl {

	/**
	 * Maximum Retry Attempts error key.
	 */
	public static final String PASSWORD_VALIDATION_ERROR_MAXIMUM_RETRY_ATTEMPTS = "PasswordValidationError_MaximumRetryAttempts";

	private SettingValueProvider<Integer> maximumFailedLoginAttemptsProvider;

	@Override
	public ValidationResult validate(final PasswordHolder passwordHolder) {
		if (passwordHolder.getFailedLoginAttempts() >= getMaximumFailedLoginAttempts()) {
			return createValidationResultWithError(PASSWORD_VALIDATION_ERROR_MAXIMUM_RETRY_ATTEMPTS);
		}
		return ValidationResult.VALID;
	}

	public Integer getMaximumFailedLoginAttempts() {
		return getMaximumFailedLoginAttemptsProvider().get();
	}

	public void setMaximumFailedLoginAttemptsProvider(final SettingValueProvider<Integer> maximumFailedLoginAttemptsProvider) {
		this.maximumFailedLoginAttemptsProvider = maximumFailedLoginAttemptsProvider;
	}

	protected SettingValueProvider<Integer> getMaximumFailedLoginAttemptsProvider() {
		return maximumFailedLoginAttemptsProvider;
	}

}
