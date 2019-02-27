/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.provider.converter.impl;

import java.util.Map;

import com.elasticpath.converter.ConversionMalformedValueException;
import com.elasticpath.converter.StringToTypeConverter;
import com.elasticpath.settings.MalformedSettingValueException;
import com.elasticpath.settings.UnexpectedSettingValueTypeException;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.provider.converter.SettingValueTypeConverter;

/**
 * Implementation of {@link SettingValueTypeConverter} that delegates to a preconfigured map.
 */
public class SettingValueTypeConverterImpl implements SettingValueTypeConverter {

	private Map<String, StringToTypeConverter<?>> stringToTypeConverterMap;

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final SettingValue settingValue) {
		try {
			if (settingValue.getValue() == null) {
				return null;
			}

			return (T) findTypeConverter(settingValue.getValueType()).convert(settingValue.getValue());
		} catch (final ConversionMalformedValueException e) {
			throw new MalformedSettingValueException(
					"Unable to convert Setting Value [" + settingValue.getPath() + "] to a value of type [" + settingValue.getValueType() + "]", e);
		}
	}

	private <T> StringToTypeConverter<T> findTypeConverter(final String settingValueType) {
		@SuppressWarnings("unchecked")
		final StringToTypeConverter<T> typeConverter = (StringToTypeConverter<T>) getStringToTypeConverterMap().get(settingValueType);

		if (typeConverter == null) {
			throw new UnexpectedSettingValueTypeException("Cannot find a converter for setting with type " + "[" + settingValueType + "].  Known "
					+ "types that can be converted: " + getStringToTypeConverterMap().keySet());
		}

		return typeConverter;
	}

	public void setStringToTypeConverterMap(final Map<String, StringToTypeConverter<?>> stringToTypeConverterMap) {
		this.stringToTypeConverterMap = stringToTypeConverterMap;
	}

	public Map<String, StringToTypeConverter<?>> getStringToTypeConverterMap() {
		return stringToTypeConverterMap;
	}

}
