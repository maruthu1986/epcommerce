/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.settings.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Maybe;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.settings.SettingsRepository;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.provider.converter.SettingValueTypeConverter;


/**
 * Implementation of SettingsRepository.
 */
@Singleton
@Named("settingsRepository")
public class SettingsRepositoryImpl implements SettingsRepository {

	private final SettingsReader settingsReader;
	private final SettingValueTypeConverter settingValueTypeConverter;

	/**
	 * Instantiates a new settings repository.
	 *
	 * @param settingsReader            the settings lookup
	 * @param settingValueTypeConverter the settings value type converter
	 */
	@Inject
	public SettingsRepositoryImpl(
			@Named("cachedSettingsReader") final SettingsReader settingsReader,
			@Named("settingValueTypeConverter") final SettingValueTypeConverter settingValueTypeConverter) {
		this.settingsReader = settingsReader;
		this.settingValueTypeConverter = settingValueTypeConverter;
	}

	@Override
	@CacheResult
	public <T> Maybe<T> getSetting(final String path, final String context) {
		return getSettingValue(path, context)
				.flatMap(settingValue -> {
					final T converted = settingValueTypeConverter.convert(settingValue);

					if (converted == null) {
						return Maybe.empty();
					} else {
						return Maybe.just(converted);
					}
				});
	}

	@SuppressWarnings("PMD.AvoidCatchingThrowable")
	private Maybe<SettingValue> getSettingValue(final String path, final String context) {
		try {
			SettingValue settingValue = settingsReader.getSettingValue(path, context);
			if (settingValue == null) {
				return Maybe.error(ResourceOperationFailure.notFound(
						"Setting value for path [" + path + "] and context [" + context + "] is not " + "found"));
			} else {
				return Maybe.just(settingValue);
			}
		} catch (Exception e) {
			return Maybe.error(ResourceOperationFailure.serverError("Unable to resolve setting value"));
		}
	}

}
