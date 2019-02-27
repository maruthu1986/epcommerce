/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.persister;

/**
 * Persists instances.
 *
 * @param <T> the generic type
 */
public interface Persister<T> {

	/**
	 * Persist an instance.
	 *
	 * @param t the thing to persist
	 */
	void persist(T t);

}
