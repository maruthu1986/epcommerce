/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.spi;

import com.elasticpath.messaging.EventType;

/**
 * A lookup service for retrieving an {@link com.elasticpath.messaging.EventType} by name.
 * @param <T> the Java type of the {@link com.elasticpath.messaging.EventType} subclass to be looked up
 */
public interface EventTypeLookup<T extends EventType> {

	/**
	 * Retrieves an {@link EventType} by name.
	 * 
	 * @param name the name by which to look up
	 * @return the corresponding {@link EventType}, or {@code null} if none exists
	 */
	T lookup(String name);

}
