/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber;

import javax.inject.Provider;

/**
 * Container for data created during an automated test scenario.
 * 
 * @param <T> the type of the instance
 */
public interface ScenarioContextValueHolder<T> extends Provider<T> {

	/**
	 * Sets an instance of {@code T}.
	 * 
	 * @param value the value of type T
	 */
	void set(T value);

}