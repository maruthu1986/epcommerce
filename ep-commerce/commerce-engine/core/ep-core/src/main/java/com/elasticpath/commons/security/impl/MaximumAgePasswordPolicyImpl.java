/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security.impl;

import java.util.Calendar;

import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Implementation of <code>PasswordPolicy</code> that validates a password's maximum age for a <code>PasswordHolder</code>.
 */
public class MaximumAgePasswordPolicyImpl extends AbstractPasswordPolicyImpl {

	/**
	 * Maximum Password Age error key.
	 */
	public static final String PASSWORD_VALIDATION_ERROR_MAXIMUM_PASSWORD_AGE = "PasswordValidationError_MaximumPasswordAge";

	private SettingValueProvider<Integer> maximumPasswordAgeDaysProvider;

	@Override
	public ValidationResult validate(final PasswordHolder passwordHolder) {
		boolean isPasswordExpired = true;
		if (passwordHolder.getLastChangedPasswordDate() != null && passwordHolder.getLastLoginDate() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(passwordHolder.getLastChangedPasswordDate());
			calendar.add(Calendar.DATE, getMaximumPasswordAgeDays());
			isPasswordExpired = calendar.getTime().compareTo(passwordHolder.getLastLoginDate()) <= 0;
		}

		if (isPasswordExpired) {
			return createValidationResultWithError(PASSWORD_VALIDATION_ERROR_MAXIMUM_PASSWORD_AGE);
		}

		return ValidationResult.VALID;
	}

	protected Integer getMaximumPasswordAgeDays() {
		return getMaximumPasswordAgeDaysProvider().get();
	}

	public void setMaximumPasswordAgeDaysProvider(final SettingValueProvider<Integer> maximumPasswordAgeDaysProvider) {
		this.maximumPasswordAgeDaysProvider = maximumPasswordAgeDaysProvider;
	}

	protected SettingValueProvider<Integer> getMaximumPasswordAgeDaysProvider() {
		return maximumPasswordAgeDaysProvider;
	}

}
