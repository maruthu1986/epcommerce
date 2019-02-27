/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.email.test.support;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Enables email sending for use in integration tests.
 */
public class EmailEnabler {

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private String assetTheme;

	/**
	 * Sets emailEnabled default value.
	 * @param defaultValue the default value to set
	 */
	public void setEmailEnabledSettingDefaultValue(final boolean defaultValue) {
		final SettingDefinition emailEnabled = settingsService.getSettingDefinition("COMMERCE/SYSTEM/emailEnabled");
		emailEnabled.setDefaultValue(String.valueOf(defaultValue));
		settingsService.updateSettingDefinition(emailEnabled);
		setAssetTheme();
	}

	/**
	 * Sets emailEnabled value.
	 * @param value the value to set.
	 */
	public void setEmailEnabledSettingsValue(final boolean value) {
		final SettingValue emailEnabledValue = settingsService.getSettingValue("COMMERCE/SYSTEM/emailEnabled");
		emailEnabledValue.setBooleanValue(value);
		settingsService.updateSettingValue(emailEnabledValue);
		setAssetTheme();
	}
	/**
	 * Sets the asset theme. This determines the Velocity templates used when constructing email contents.
	 */
	private void setAssetTheme() {
		final SettingDefinition settingsDefTheme = settingsService.getSettingDefinition("COMMERCE/STORE/theme");
		settingsDefTheme.setDefaultValue(assetTheme);
		settingsService.updateSettingDefinition(settingsDefTheme);
	}

}
