/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat;

import javax.inject.Provider;

/**
 * Container for data created during an automated test scenario.
 */
public interface ScenarioContextValueHolder<T> extends Provider<T> {

	/**
	 * Sets an instance of {@code T}.
	 */
	void set(T value);

}
