/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.provider.impl;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.provider.SettingValueProvider;
import com.elasticpath.settings.provider.converter.SettingValueTypeConverter;

/**
 * Implementation of {@link SettingValueProvider} that delegates to a {@link SettingsReader}.
 *
 * @param <T> the type of value expected to be provided
 */
public class SettingValueProviderImpl<T> implements SettingValueProvider<T> {

	private String path;

	private String context;

	private SettingsReader settingsReader;

	private SettingValueTypeConverter settingValueTypeConverter;

	@Override
	public T get() {
		return get(null);
	}

	@Override
	public T get(final String context) {
		verifyDependencies();

		final SettingValue settingValue = getSettingValue(context);

		return getSettingValueTypeConverter().convert(settingValue);
	}

	private SettingValue getSettingValue(final String context) {
		if (!StringUtils.isBlank(context)) {
			return getSettingsReader().getSettingValue(getPath(), context);
		}

		if (!StringUtils.isBlank(getContext())) {
			return getSettingsReader().getSettingValue(getPath(), getContext());
		}

		return getSettingsReader().getSettingValue(getPath());
	}

	private void verifyDependencies() {
		if (getSettingsReader() == null) {
			throw new IllegalStateException("settingsReader field must not be null");
		}

		if (getSettingValueTypeConverter() == null) {
			throw new IllegalStateException("settingsValueTypeConverter field must not be null");
		}

		if (getPath() == null) {
			throw new IllegalStateException("path field must not be null");
		}
	}

	public void setPath(final String path) {
		this.path = path;
	}

	protected String getPath() {
		return path;
	}

	public void setContext(final String context) {
		this.context = context;
	}

	protected String getContext() {
		return context;
	}

	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	public void setSettingValueTypeConverter(final SettingValueTypeConverter settingValueTypeConverter) {
		this.settingValueTypeConverter = settingValueTypeConverter;
	}

	public SettingValueTypeConverter getSettingValueTypeConverter() {
		return settingValueTypeConverter;
	}
}
