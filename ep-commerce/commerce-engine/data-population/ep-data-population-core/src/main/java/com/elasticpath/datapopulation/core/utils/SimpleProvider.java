/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import javax.inject.Provider;

/**
 * A simple {@link Provider} implementation which just returns what has been set on it.
 * Can be used when a {@link Provider} has been explicitly requested.
 *
 * @param <T> the type of object that is returned by this {@link Provider}.
 */
public class SimpleProvider<T> implements Provider<T> {
	private final T delegate;

	/**
	 * Constructor which takes in the object to return in the {@link #get()} method.
	 *
	 * @param delegate the object to return by {@link #get()}.
	 */
	public SimpleProvider(final T delegate) {
		this.delegate = delegate;
	}

	@Override
	public T get() {
		return this.delegate;
	}
}
