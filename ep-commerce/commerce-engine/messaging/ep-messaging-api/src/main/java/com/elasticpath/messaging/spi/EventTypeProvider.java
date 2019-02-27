/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.spi;

import com.elasticpath.messaging.EventType;

/**
 * Provider interface for introducing new {@link EventType} classes.
 *
 * @param <T> the type of {@link EventType}
 */
public interface EventTypeProvider<T extends EventType> {

	/**
	 * Returns the class of the {@link EventType} to be introduced.
	 *
	 * @return the event type class
	 */
	Class<T> getEventTypeClass();

	/**
	 * Returns the {@link EventTypeLookup} that can locate instances of the introduced {@link EventType} by name.
	 *
	 * @return an {@link EventTypeLookup} instance
	 */
	EventTypeLookup<T> getEventTypeLookup();

}
