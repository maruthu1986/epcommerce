/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.refreshstrategy.impl;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.settings.domain.SettingValue;

/**
 * Immutable setting value data for caching.
 */
public class SettingCacheData {

	private final String path;
	private final SettingValue defaultValue;
	private final Map<String, SettingValue> contextValues;

	/**
	 * Constructor.
	 *
	 * @param path the setting definition path
	 * @param defaultValue the default, fallback setting value
	 * @param contextValues all contextual setting values
	 */
	protected SettingCacheData(final String path, final SettingValue defaultValue, final Map<String, SettingValue> contextValues) {
		this.path = path;
		this.defaultValue = defaultValue;

		// Converts all context keys to lower case.
		this.contextValues = contextValues.entrySet().stream()
				.collect(Collectors.toMap(entry -> StringUtils.lowerCase(entry.getKey()), Map.Entry::getValue));
	}

	/**
	 * Retrieve a setting value by context, falling back to the default value if no matching context value exists.
	 *
	 * @param context the setting context
	 * @return a SettingValue
	 */
	protected SettingValue getSettingValue(final String context) {
		SettingValue settingValue = Optional.ofNullable(getContextValues().get(StringUtils.lowerCase(context)))
				.orElse(getDefaultValue());
		if (settingValue.getContext() == null) {
			settingValue.setContext(context);
		}
		
		return settingValue;
	}

	protected String getPath() {
		return path;
	}

	protected SettingValue getDefaultValue() {
		return defaultValue;
	}

	protected Map<String, SettingValue> getContextValues() {
		return contextValues;
	}

}
