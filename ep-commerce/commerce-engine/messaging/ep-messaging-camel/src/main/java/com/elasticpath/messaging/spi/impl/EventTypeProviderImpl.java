/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.spi.impl;

import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.spi.EventTypeLookup;
import com.elasticpath.messaging.spi.EventTypeProvider;

/**
 * Simple POJO implementation of {@link EventTypeProvider}.
 *
 * @param <T> the type of {@link EventType}
 */
public class EventTypeProviderImpl<T extends EventType> implements EventTypeProvider<T> {

	private Class<T> eventTypeClass;

	private EventTypeLookup<T> eventTypeLookup;

	/**
	 * Default constructor.
	 */
	public EventTypeProviderImpl() {
		super();
	}

	/**
	 * Constructor with {@link EventType} class and lookup.
	 *
	 * @param eventTypeClass the {@link EventType} class
	 * @param eventTypeLookup the {@link EventType} lookup
	 */
	public EventTypeProviderImpl(final Class<T> eventTypeClass, final EventTypeLookup<T> eventTypeLookup) {
		this.eventTypeClass = eventTypeClass;
		this.eventTypeLookup = eventTypeLookup;
	}

	@Override
	public Class<T> getEventTypeClass() {
		return eventTypeClass;
	}

	public void setEventTypeClass(final Class<T> eventTypeClass) {
		this.eventTypeClass = eventTypeClass;
	}

	@Override
	public EventTypeLookup<T> getEventTypeLookup() {
		return eventTypeLookup;
	}

	public void setEventTypeLookup(final EventTypeLookup<T> eventTypeLookup) {
		this.eventTypeLookup = eventTypeLookup;
	}

}
