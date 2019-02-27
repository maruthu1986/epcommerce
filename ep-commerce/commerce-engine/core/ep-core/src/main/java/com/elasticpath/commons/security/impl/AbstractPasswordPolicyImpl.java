/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.security.PasswordPolicy;
import com.elasticpath.commons.security.ValidationError;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.commons.util.PasswordGenerator;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Base abstract implementation of <code>PasswordPolicy</code> that validates the password for a <code>PasswordHolder</code>.
 */
public abstract class AbstractPasswordPolicyImpl implements PasswordPolicy {

	private BeanFactory beanFactory;

	private SettingsService settingsService;

	private PasswordGenerator passwordGenerator;

	private SettingValueProvider<Integer> minimumPasswordLengthProvider;

	/**
	 * @return settingsService
	 * @deprecated inject a SettingValueProvider<T> to satisfy the required setting value as per
	 * {@link #setMinimumPasswordLengthProvider(SettingValueProvider)}
	 */
	@Deprecated
	public SettingsService getSettingsService() {
		return settingsService;
	}

	/**
	 * @param settingsService <code>SettingsService</code>
	 * @deprecated inject a SettingValueProvider<T> to satisfy the required setting value as per
	 * {@link #setMinimumPasswordLengthProvider(SettingValueProvider)}
	 */
	@Deprecated
	public void setSettingsService(final SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	/**
	 * Gets configured password generator. The <code>minimumPasswordLength</code> is set up properly for that impl.
	 * 
	 * @return passwordGenerator configured password generator
	 */
	@Override
	public PasswordGenerator getPasswordGenerator() {
		passwordGenerator.setMinimumPasswordLength(getMinimumPasswordLength());
		return passwordGenerator;
	}

	/**
	 * Returns the minimum password length.
	 * 
	 * @return the minimum password length
	 */
	protected Integer getMinimumPasswordLength() {
		return getMinimumPasswordLengthProvider().get();
	}

	/**
	 * @param passwordGenerator <code>passwordGenerator</code>
	 */
	public void setPasswordGenerator(final PasswordGenerator passwordGenerator) {
		this.passwordGenerator = passwordGenerator;
	}

	/**
	 * Gets setting value.
	 *
	 * @param path the path of the setting
	 * @return the value for a given path and context
	 * @deprecated inject a SettingValueProvider<T> to satisfy the required setting value as per
	 * {@link #setMinimumPasswordLengthProvider(SettingValueProvider)}
	 */
	@Deprecated
	protected String getSettingValue(final String path) {
		return settingsService.getSettingValue(path).getValue();
	}

	/**
	 * Creates a ValidationResult containing one error.
	 * 
	 * @param errorKey the error key
	 * @return the validation result
	 */
	protected ValidationResult createValidationResultWithError(final String errorKey) {
		ValidationResult validationResult = new ValidationResult();
		validationResult.addError(new ValidationError(errorKey));
		return validationResult;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setMinimumPasswordLengthProvider(final SettingValueProvider<Integer> minimumPasswordLengthProvider) {
		this.minimumPasswordLengthProvider = minimumPasswordLengthProvider;
	}

	protected SettingValueProvider<Integer> getMinimumPasswordLengthProvider() {
		return minimumPasswordLengthProvider;
	}

}
