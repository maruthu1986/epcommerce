/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.provider.converter;

import com.elasticpath.settings.domain.SettingValue;

/**
 * Converter that will return a {@link SettingValue}'s value as a given type.
 */
public interface SettingValueTypeConverter {

	/**
	 * Convert the given {@link SettingValue} to the required type.
	 *
	 * @param settingValue the setting value to convert
	 * @param <T> the type the {@link SettingValue}'s value will be converted to
	 * @return the {@link SettingValue}'s value converted to the given type.
	 */
	<T> T convert(SettingValue settingValue);

}