/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;

/**
 * A POJO implementation of <code>EventMessage</code>.
 */
public class EventMessageImpl implements EventMessage, Serializable {

	private static final long serialVersionUID = 50000000001L;

	private EventType eventType;

	private String guid;

	private Map<String, Object> data;

	/**
	 * Creates a new {@code EventMessageImpl} given an {@code EventType} and {@code guid}.
	 * 
	 * @param eventType the event type
	 * @param guid the guid
	 */
	public EventMessageImpl(final EventType eventType, final String guid) {
		this(eventType, guid, null);
	}

	/**
	 * Creates a new {@link EventMessage} instance with data.
	 * 
	 * @param eventType the event type
	 * @param guid the object GUID
	 * @param data the data map
	 */
	public EventMessageImpl(final EventType eventType, final String guid, final Map<String, Object> data) {
		this.eventType = eventType;
		this.guid = guid;

		if (data == null) {
			this.data = Collections.emptyMap();
		} else {
			this.data = data;
		}
	}

	@Override
	public EventType getEventType() {
		return eventType;
	}

	@Override
	public String getGuid() {
		return guid;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(final Object object) {
		return EqualsBuilder.reflectionEquals(this, object);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public Map<String, Object> getData() {
		return data;
	}

	public void setEventType(final EventType eventType) {
		this.eventType = eventType;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public void setData(final Map<String, Object> data) {
		this.data = data;
	}

}
