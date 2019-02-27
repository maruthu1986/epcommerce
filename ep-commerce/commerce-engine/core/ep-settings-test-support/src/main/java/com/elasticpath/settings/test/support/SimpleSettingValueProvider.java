/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.test.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Simple implementation of the Provider interface for use within tests.
 *
 * @param <T> the type of instance to be provided
 */
public class SimpleSettingValueProvider<T> implements SettingValueProvider<T> {

	private final Map<String, T> instanceMap;

	/**
	 * Constructor.  The parameter will be returned by {@link #get} without modification.
	 *
	 * @param instance the instance that should be returned by {@link #get}
	 */
	public SimpleSettingValueProvider(final T instance) {
		instanceMap = new HashMap<>();
		instanceMap.put(null, instance);
	}

	/**
	 * Constructor.  The values of the parameter map will be returned by {@link #get(String)} without modification.  Calls to {@link #get} will
	 * attempt to retrieve the map element with a key of {@code null}.
	 *
	 * @param instanceMap the map of instances that should be returned by {@link #get(String)}
	 */
	public SimpleSettingValueProvider(final Map<String, T> instanceMap) {
		this.instanceMap = instanceMap;
	}

	/**
	 * Constructor.  The parameter map will be returned by {@link #get(String)} without modification.  Calls to {@link #get} will
	 * return {@code null}.
	 *
	 * @param context the expected context parameter of a subsequent call to {@link #get(String)}
	 * @param value the instances that should be returned by {@link #get(String)}
	 */
	public SimpleSettingValueProvider(final String context, final T value) {
		this.instanceMap = Collections.singletonMap(context, value);
	}

	@Override
	public T get() {
		return instanceMap.get(null);
	}

	@Override
	public T get(final String context) {
		return instanceMap.get(context);
	}

}
