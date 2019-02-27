/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.provider;

/**
 * Provides instances of Settings values.
 *
 * @param <T> the type of value expected to be provided
 */
public interface SettingValueProvider<T> {

	/**
	 * <p>Provides a fully-constructed instance of a Setting value.</p>
	 * <p>Repeated calls to this method will respect the underlying cache strategy; for example, fresh values will be returned after the configured
	 * timeout expires.</p>
	 *
	 * @return a Setting value of the type expected to be provided
	 */
	T get();

	/**
	 * <p>Provides a fully-constructed instance of a Setting value within a specified context qualifier.</p>
	 * <p>Repeated calls to this method will respect the underlying cache strategy; for example, fresh values will be returned after the configured
	 * timeout expires.</p>
	 *
	 * @param context the context qualifier within which to locate the appropriate setting value
	 * @return a Setting value of the type expected to be provided
	 */
	T get(String context);

}
