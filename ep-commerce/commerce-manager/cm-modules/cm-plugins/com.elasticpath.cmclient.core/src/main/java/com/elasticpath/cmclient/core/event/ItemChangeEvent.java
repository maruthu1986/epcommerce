/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.event;

import java.util.EventObject;

/**
 * Item change event object.
 * 
 * @param <T> the type of event that is changing
 */
public class ItemChangeEvent<T> extends EventObject {
	
	private static final long serialVersionUID = 1L;

	private final T item;
	
	private final EventType eventType;
	
	/**
	 * Constructs a new "change" event. The event type is assumed to be {@link EventType#CHANGE}.
	 *
	 * @param source the source object of this event
	 * @param item the {@link T} that is changing
	 */
	public ItemChangeEvent(final Object source, final T item) {
		this(source, item, EventType.CHANGE);
	}

	/**
	 * Constructs new event with a configurable event type.
	 * 
	 * @param source the source object of this event
	 * @param item the {@link T} that is changing
	 * @param eventType the type of event
	 */
	public ItemChangeEvent(final Object source, final T item, final EventType eventType) {
		super(source);
		this.item = item;
		this.eventType = eventType;
	}
	
	/**
	 * Gets the {@link T} that has changed.
	 *
	 * @return the {@link T} that has changed
	 */
	public T getItem() {
		return item;
	}
	
	/**
	 * Gets the type of change.
	 *
	 * @return the type of change
	 */
	public EventType getEventType() {
		return eventType;
	}
	
	/**
	 * The type of change.
	 */
	public enum EventType {
		/** A new item that has never been saved before has been added. */
		ADD,
		/** A previously saved item has been removed. */
		REMOVE,
		/** An item has been changed. It has not be added or removed. */
		CHANGE;
	}
}
