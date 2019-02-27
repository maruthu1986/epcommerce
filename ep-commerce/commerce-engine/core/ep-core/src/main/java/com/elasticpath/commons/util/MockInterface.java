/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.commons.util;

/**
 * Interface for mocking methods.
 */
public interface MockInterface {
	
	/**
	 * The mock method.
	 * 
	 * @param objects the array of objects (arguments)
	 * @param <T> template arg.
	 * @return object return value
	 */
	<T> T method(Object ... objects);
}
